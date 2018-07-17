package io.kuban.projection.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.kuban.projection.CustomerApplication;
import io.kuban.projection.R;
import io.kuban.projection.model.TabletInformationModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 版本更新管理
 * Created by wangxuan on 16/10/12.
 */
public class CheckUpdate {
    private final String TAG = this.getClass().getSimpleName();

    private ProgressDialog progressDialog = null;
    private ACache cache;
    private Context context;
    private Cancel cancel;

    public static final int ACTION_UPDATE = 1;
    public static final int REQUEST_UPDATE = 3;


    public CheckUpdate(Context context, TabletInformationModel versionModel) {

        if (versionModel == null) {
            return;
        }

        this.context = context;
        cache = ACache.get(context);
        //模拟获取服务器版本
        cache.put(ACache.VERSIONT_YPE_PREF, versionModel.force_update);//是否强制更新
        cache.put(ACache.VERSIONT_URL_PREF, versionModel.android_update_url);
        cache.put(ACache.VERSIONT_TIP_PREF, "发现最新版本，邀您体验");
        cache.put(ACache.APPVERSION_PREF, versionModel.version);//服务器版本
        checkUpdate();//自动检测版本升级
    }

    public void setCancel(Cancel cancel) {
        this.cancel = cancel;
    }

    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case ACTION_UPDATE:
                    progressDialog.setProgress(msg.arg1);
                    progressDialog.setProgressNumberFormat(msg.obj + "");
                    break;
            }
        }
    };

    /**
     * 检测版本
     */
    private void checkUpdate() {

        boolean versionType = cache.getAsBoolean(ACache.VERSIONT_YPE_PREF);//获取是否强制升级
        final String apkUrl = cache.getAsString(ACache.VERSIONT_URL_PREF);//获取下载地址
        if (UpdateUtil.checkUpdate(context)) {
            if (!TextUtils.isEmpty(apkUrl)) {
                String updateTipPref = cache.getAsString(ACache.VERSIONT_TIP_PREF);//获取更新介绍

                if (!versionType) {//非强制
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(TextUtils.isEmpty(updateTipPref) ? context.getString(R.string.version_update) : updateTipPref);
                    builder.setPositiveButton(R.string.new_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downloadFile(apkUrl);
                            dialog.dismiss();
//                            User.saveUserFirstPref(MainActivity.this, true);
                        }
                    });
                    builder.setNegativeButton(R.string.cancle_refresh, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();

                } else {//强制
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setTitle(TextUtils.isEmpty(updateTipPref) ? context.getString(R.string.version_update) : updateTipPref);
                    builder.setPositiveButton(R.string.new_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downloadFile(apkUrl);
                            dialog.dismiss();
//                            User.saveUserFirstPref(MainActivity.this, true);
                        }
                    });
//                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (cancel != null) {
//                                cancel.updateFinish();
//                            }
//                        }
//                    });
                    builder.show();
                }
            }
        }
    }


    /**
     * 提示下载
     *
     * @param url
     */
    void downloadFile(final String url) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
        }
        OkHttpClient mOkHttpClient = new OkHttpClient();
        progressDialog.setTitle(R.string.down_apk);
        progressDialog.show();
        progressDialog.onStart();
        Request request = new Request.Builder().url(url).build();
        //TODO: check if this is using streaming
        //or use retrofit: https://futurestud.io/tutorials/retrofit-2-how-to-download-files-from-server
        // @Streaming
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                //TODO: handle error with UI feedback
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                try {
                    ResponseBody body = response.body();
                    is = body.byteStream();
                    long total = body.contentLength();
                    File file = new File(SDPath, (CustomerApplication.getStringResources(R.string.app_name) + ".apk"));
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        Message msg = handler.obtainMessage();
                        msg.what = ACTION_UPDATE;
                        msg.obj = FileSizeConversion.FormetFileSize(total);
                        msg.arg1 = progress;
                        handler.sendMessage(msg);
                    }
                    fos.flush();
                    downloadCompleted();
                } catch (Exception e) {
                    //TODO: handle error with UI feedback
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to close download stream");
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to close download stream");
                    }
                }
            }


        });
    }

    private void downloadCompleted() {
        handler.post(new Runnable() {
            public void run() {
                progressDialog.cancel();
//                dialog.dismiss();
                if (cancel != null) {
                    cancel.update();
                }
            }
        });
    }


    public interface Cancel {
        void updateFinish();

        void update();
    }

}
