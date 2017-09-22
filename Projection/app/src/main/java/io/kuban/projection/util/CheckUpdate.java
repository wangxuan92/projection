package io.kuban.projection.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

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

/**
 * 版本更新管理
 * Created by wangxuan on 16/10/12.
 */
public class CheckUpdate {
    private ProgressDialog progressDialog = null;
    private ACache cache;
    private Context context;
    private Cancel cancel;
    public static final String APK_FILENAME = "kuban.apk";
    public static final int ACTION_UPDATE = 1;
    public static final int REQUEST_UPDATE = 3;


    public CheckUpdate(Context context, TabletInformationModel versionModel) {
        this.context = context;
        cache = ACache.get(context);
        //模拟获取服务器版本
        cache.put(ACache.VERSIONT_YPE_PREF, 0);//是否强制更新
//        cache.put(ACache.VERSIONT_URL_PREF,"http://weixin.qq.com/cgi-bin/readtemplate?uin=&stype=&promote=&fr=www.baidu.com&lang=zh_CN&ADTAG=&check=false&t=weixin_download_method&sys=android&loc=weixin,android,web,0");
        if (TextUtils.isEmpty(versionModel.app_download_url)) {
            Toast.makeText(context, CustomerApplication.getStringResources(R.string.no_new_version), Toast.LENGTH_SHORT).show();
            return;
        }
        cache.put(ACache.VERSIONT_URL_PREF, versionModel.app_download_url);
        cache.put(ACache.VERSIONT_TIP_PREF, "发现最新版本，邀您体验");
        cache.put(ACache.APPVERSION_PREF, versionModel.app_version);//服务器版本
        checkUpdate(versionModel);//自动检测版本升级
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
    private void checkUpdate(TabletInformationModel versionModel) {

        int versionType = cache.getAsInteger(ACache.VERSIONT_YPE_PREF);//获取是否强制升级
        final String apkUrl = cache.getAsString(ACache.VERSIONT_URL_PREF);//获取下载地址
//        final String apkUrl = "http://imtt.dd.qq.com/16891/A17F103DE676F7E48DAC714FAC1B1993.apk?fsname=io.kuban.client_1.0_1.apk&csr=4d5s";//获取下载地址
        if (UpdateUtil.checkUpdate(context, versionModel.app_version)) {
            if (!TextUtils.isEmpty(apkUrl)) {
                String updateTipPref = cache.getAsString(ACache.VERSIONT_TIP_PREF);//获取更新介绍

                if (versionType == 0) //非强制
                {
//                    DialogUtil.AlertDialog(context, TextUtils.isEmpty(updateTipPref) ? context.getString(R.string.version_update) : updateTipPref, new DialogUtil.DialogUtilOnclickListener() {
//                        @Override
//                        public void onClick() {
//                    downFile(apkUrl, Setting.getAppversionPref(context) + APK_FILENAME);
//                        }
//                    }, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(TextUtils.isEmpty(updateTipPref) ? context.getString(R.string.version_update) : updateTipPref);
                    builder.setPositiveButton(R.string.new_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downFile(apkUrl, APK_FILENAME);
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
//                    DialogUtil.AlertDialogWithCancle(context, TextUtils.isEmpty(updateTipPref) ? context.getString(R.string.version_update) : updateTipPref, new DialogUtil.DialogUtilOnclickListener() {
//                        @Override
//                        public void onClick() {
//                            downFile(apkUrl, Setting.getAppversionPref(context) + APK_FILENAME);
//                        }
//                    }, new DialogInterface.OnCancelListener() {
//                        @Override
//                        public void onCancel(DialogInterface dialog) {
//
//                        }
//                    });
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setTitle(TextUtils.isEmpty(updateTipPref) ? context.getString(R.string.version_update) : updateTipPref);
                    builder.setPositiveButton(R.string.new_confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downFile(apkUrl, APK_FILENAME);
                            dialog.dismiss();
//                            User.saveUserFirstPref(MainActivity.this, true);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (cancel != null) {
                                cancel.updateFinish();
                            }
                        }
                    });
                    builder.show();
                }
            }

        }


    }


    /**
     * 提示下载
     *
     * @param url
     * @param FileName
     */
    void downFile(final String url, final String FileName) {
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
        mOkHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(SDPath, "kuban.apk");
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
                    down();
                } catch (Exception e) {
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }


        });
    }

    void down() {
        handler.post(new Runnable() {
            public void run() {
                progressDialog.cancel();
//                dialog.dismiss();
                cancel.update();
            }
        });
    }


    public interface Cancel {
        public void updateFinish();

        public void update();
    }

}
