package io.kuban.projection.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.kuban.projection.CustomerApplication;
import io.kuban.projection.R;
import io.kuban.projection.base.ActivityManager;
import io.kuban.projection.base.Constants;
import io.kuban.projection.model.ChooseModel;
import io.kuban.projection.model.TabletInformationModel;
import io.kuban.projection.service.AlwaysOnService.Bootstrap;
import io.kuban.projection.util.CheckUpdate;
import io.kuban.projection.util.ErrorUtil;
import io.kuban.projection.util.ToastUtils;
import io.kuban.projection.util.UpdateUtil;
import io.kuban.projection.util.UserManager;
import io.kuban.projection.util.Utils;
import io.kuban.projection.view.InputNameDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 绑定会议室界面
 * <p>
 * Created by wangxuan on 16/10/28.
 */
public class ChooseActivity extends BaseCompatActivity implements CheckUpdate.Cancel {
    @BindView(R.id.choose_list)
    ListView listView;
    @BindView(R.id.linear2)
    LinearLayout listViewLinear;
    @BindView(R.id.input_url)
    EditText inputUrl;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.change)
    Button change;
    @BindView(R.id.version)
    Button version;
    @BindView(R.id.determine)
    Button determine;
    @BindView(R.id.choose_type)
    TextView chooseType;
    @BindView(R.id.prompt)
    TextView prompt;
    @BindView(R.id.area_name)
    TextView areaName;

    private List<ChooseModel> chooseModelList = new ArrayList<>();
    private boolean isUpdateApp = false;
    private String app_id;
    private String app_secret;
    private MyAdapter adapter;
    private boolean isSelectBranch = true;
    private boolean isJumpBack = true;
    private String meeting_rooms_id;
    private String location_id;
    private InputNameDialog dialog;
    private TabletInformationModel tabletInformationModel;//查询到的平板信息

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_activity);
        ButterKnife.bind(this);
        chooseModelList.addAll(Constants.lListcChooseMode);
        app_id = UserManager.getAppId();
        app_secret = UserManager.getAppSecret();
        if (null != getIntent().getExtras()) {
            isJumpBack = getIntent().getExtras().getBoolean(Constants.IS_JUMP_BACK);
        }
        String app_id = UserManager.getAppId();
        String app_secret = UserManager.getAppSecret();
        http(app_id, app_secret);
        inputUrl(getServer());
        setVisibility(TextUtils.isEmpty(getAreaName()));

        setFocusable(false);
        version.setText(UpdateUtil.getVersionName(this));
        adapter = new MyAdapter(this);
        listView.setAdapter(adapter);
        chooseType.setText(CustomerApplication.getStringResources(R.string.location));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isSelectBranch) {
                    //选择分店
                    http(chooseModelList.get(position).id);
                    location_id = chooseModelList.get(position).id;
                } else {
                    //选择会议室
                    chooseType.setText(CustomerApplication.getStringResources(R.string.meeting_room));
                    meeting_rooms_id = chooseModelList.get(position).id;
                    if (TextUtils.isEmpty(getAreaId())) {
                        tabletInformation(chooseModelList.get(position).name + "电视投屏");
                    } else {
                        updateTabletInformation(chooseModelList.get(position).name + "电视投屏");
                    }

                }
            }
        });
        determine.setFocusable(true);
        determine.setFocusableInTouchMode(true);
        determine.requestFocus();
        determine.requestFocusFromTouch();
    }


    /**
     * 纪录平板信息
     *
     * @param device_name
     */
    private void tabletInformation(String device_name) {
        showProgressDialog();
        Map<String, String> queries = new HashMap<>();
        queries.put("area_id", meeting_rooms_id);
        queries.put("location_id", location_id);
        queries.put("device_name", device_name);
        queries.put("device_type", Constants.SHARE);
        queries.put("device_id", CustomerApplication.device_id);
        Call<TabletInformationModel> announcementsCall = getKubanApi().sendRecordTabletInformation(app_id, app_secret, queries);
        announcementsCall.enqueue(new Callback<TabletInformationModel>() {
            @Override
            public void onResponse(Call<TabletInformationModel> call, Response<TabletInformationModel> response) {
                if (response.isSuccessful()) {
                    recordData(response.body());
                    Log.e(LOG_TAG, " 记录平板信息成功 ");
                } else {
                    ErrorUtil.handleError(activity, response);
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<TabletInformationModel> call, Throwable t) {
                Log.e(LOG_TAG, " Throwable  " + t);
                ErrorUtil.handleError(activity, t);
                dismissProgressDialog();
            }
        });
    }

    /**
     * 更新平板信息
     *
     * @param device_name
     */
    private void updateTabletInformation(String device_name) {
        showProgressDialog();
        Map<String, String> queries = new HashMap<>();
        queries.put("area_id", meeting_rooms_id);
        queries.put("location_id", location_id);
        queries.put("device_name", device_name);
        queries.put("device_type", Constants.SHARE);
        queries.put("device_id", CustomerApplication.device_id);
        Call<TabletInformationModel> announcementsCall = getKubanApi().updateTabletInformation(CustomerApplication.device_id, app_id, app_secret, queries);
        announcementsCall.enqueue(new Callback<TabletInformationModel>() {
            @Override
            public void onResponse(Call<TabletInformationModel> call, Response<TabletInformationModel> response) {
                if (response.isSuccessful()) {
                    recordData(response.body());
                    Log.e(LOG_TAG, " 更新平板信息成功 ");
                } else {
                    ErrorUtil.handleError(activity, response);
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<TabletInformationModel> call, Throwable t) {
                Log.e(LOG_TAG, " Throwable  " + t);
                ErrorUtil.handleError(activity, t);
                dismissProgressDialog();
            }
        });
    }

    /**
     * 查询平板信息
     *
     * @param app_id
     * @param app_secret
     */
    public void http(final String app_id, final String app_secret) {
        Call<TabletInformationModel> createSessionCall = getKubanApi().getTabletInformation(CustomerApplication.device_id, app_id, app_secret);
        createSessionCall.enqueue(new Callback<TabletInformationModel>() {
            @Override
            public void onResponse(Call<TabletInformationModel> call, Response<TabletInformationModel> response) {
                if (response.isSuccessful()) {
                    if (!TextUtils.isEmpty(response.body().id)) {
                        tabletInformationModel = response.body();
                        updateAreaName(tabletInformationModel.area_name);
                        setAreaId(tabletInformationModel.area_id, tabletInformationModel.area_name);
                        if (UpdateUtil.checkUpdate(activity, tabletInformationModel.app_version)) {
                            prompt.setVisibility(View.VISIBLE);
                        } else {
                            prompt.setVisibility(View.GONE);
                        }
                    }
                } else {
                    ErrorUtil.handleError(activity, response);
                }
            }

            @Override
            public void onFailure(Call<TabletInformationModel> call, Throwable t) {
                ErrorUtil.handleError(activity, t);
            }
        });
    }

    private void recordData(TabletInformationModel tabletInformationModel) {
        setAreaId(tabletInformationModel.area_id, tabletInformationModel.area_name);
        updateAreaName(tabletInformationModel.area_name);
        setVisibility(false);
    }

    private void updateAreaName(String name) {
        areaName.setText(CustomerApplication.getStringResources(R.string.the_current_meeting_room) + name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.lListcChooseMode = null;
    }

    /**
     * 可选会议室
     *
     * @param location_id
     */
    private void http(String location_id) {
        showProgressDialog();
        Call<List<ChooseModel>> createSessionCall = getKubanApi().getOptionalMeetingRooms(location_id, app_id, app_secret);
        createSessionCall.enqueue(new Callback<List<ChooseModel>>() {
            @Override
            public void onResponse(Call<List<ChooseModel>> call, Response<List<ChooseModel>> response) {
                dismissProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        chooseModelList.clear();
                        chooseModelList.addAll(response.body());
                        adapter.notifyDataSetChanged();
                        isSelectBranch = false;
                    } else {
                        ToastUtils.showShort(ChooseActivity.this, CustomerApplication.getStringResources(R.string.no_optional_conference_room));
                    }
                } else {
                    ErrorUtil.handleError(activity, response);
                }
            }

            @Override
            public void onFailure(Call<List<ChooseModel>> call, Throwable t) {
                Log.e(LOG_TAG, call + "onFailure" + t);
                ErrorUtil.handleError(activity, t);
                dismissProgressDialog();
            }
        });
    }

    protected void inputUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            inputUrl.setText(url);
        }
    }


    private void determineClick() {
        if (TextUtils.isEmpty(getAreaId())) {
            ToastUtils.showShort(ChooseActivity.this, CustomerApplication.getStringResources(R.string.no_binding_meeting_room));
            return;
        }
        if (!isJumpBack) {
//            ActivityManager.startXWalkViewActivity(ChooseActivity.this, new Intent(), getUrl());
            ActivityManager.startAgoraScreenSharingActivity(ChooseActivity.this, new Intent(), getUrl());
        }
        setUrl(Utils.saveUrl(getServer(), getAreaId()));
        finish();
    }


    public void setFocusable(boolean isFocusable) {
        inputUrl.setEnabled(isFocusable);
        inputUrl.setFocusable(isFocusable);
        inputUrl.setFocusableInTouchMode(isFocusable);
    }

    public void setVisibility(boolean isVisibility) {
        if (isVisibility) {
            change.setVisibility(View.GONE);
            listViewLinear.setVisibility(View.VISIBLE);
        } else {
            change.setVisibility(View.VISIBLE);
            listViewLinear.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.determine, R.id.save, R.id.imag_return, R.id.change, R.id.version})
    public void back(View view) {
        final String server = inputUrl.getText().toString().trim();
        switch (view.getId()) {
            case R.id.determine:
                if (!server.equals(getServer())) {
                    dialog = new InputNameDialog(this, CustomerApplication.getStringResources(R.string.url_prompt));
                    dialog.setOnPositiveListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.remove();
                            inputUrl(getServer());
                        }
                    });
                    dialog.setOnNegativeListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.remove();
                            setServer(server);
                            determineClick();
                        }
                    });
                    dialog.show();
                    return;
                }
                determineClick();
                break;
            case R.id.save:
                if (inputUrl.isEnabled()) {
                    setFocusable(false);
                    save.setText(CustomerApplication.getStringResources(R.string.editor_url));
                } else {
                    setFocusable(true);
                    save.setText(CustomerApplication.getStringResources(R.string.save_url));
                }
                if (!server.equals(getServer())) {
                    setServer(server);
                }
                break;
            case R.id.imag_return:
                if (isSelectBranch) {
                    finish();
                } else {
                    chooseModelList.clear();
                    chooseModelList.addAll(Constants.lListcChooseMode);
                    adapter.notifyDataSetChanged();
                    isSelectBranch = true;
                    chooseType.setText(CustomerApplication.getStringResources(R.string.location));
                }
                break;
            case R.id.change:
                setVisibility(true);
                break;
            case R.id.version:
                if (null != tabletInformationModel) {
                    //检测版本
                    CheckUpdate checkUpdate = new CheckUpdate(this, tabletInformationModel);
                    checkUpdate.setCancel(this);
                }
                break;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (isUpdateApp) {
//            Bootstrap.startAlwaysOnService(this, "Main");
//        }
    }

    @Override
    public void updateFinish() {
        finish();
    }

    @Override
    public void update() {
//        isUpdateApp = true;
//        Bootstrap.stopAlwaysOnService(this);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), CheckUpdate.APK_FILENAME)),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private class MyAdapter extends BaseAdapter {

        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        //        private TextView number;
        private TextView chooseNumber;
        private TextView chooseName;
        private LayoutInflater inflater;

        @Override
        public int getCount() {
            return chooseModelList.size();
        }

        @Override
        public Object getItem(int position) {
            return chooseModelList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.choose_item, null);
            }
            ChooseModel cm = chooseModelList.get(position);
//            number = (TextView) convertView.findViewById(R.id.number);
            chooseName = (TextView) convertView.findViewById(R.id.choose_name);
            chooseNumber = (TextView) convertView.findViewById(R.id.choose_number);
            chooseName.setText(cm.name + "");
            if (isSelectBranch) {
                chooseNumber.setText(cm.areas_count + "会议室");
            } else {
                chooseNumber.setText("");
            }
            return convertView;
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (isSelectBranch) {
                finish();
            } else {
                chooseModelList.clear();
                chooseModelList.addAll(Constants.lListcChooseMode);
                adapter.notifyDataSetChanged();
                isSelectBranch = true;
                chooseType.setText(CustomerApplication.getStringResources(R.string.location));
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}