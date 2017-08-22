package io.kuban.projection.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
import io.kuban.projection.util.ToastUtils;
import io.kuban.projection.util.UserManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 绑定会议室界面
 * <p>
 * Created by wangxuan on 16/10/28.
 */
public class ChooseActivity extends BaseCompatActivity {
    @BindView(R.id.choose_list)
    ListView listView;

    private List<ChooseModel> chooseModelList = new ArrayList<>();
    private String app_id;
    private String app_secret;
    private MyAdapter adapter;
    private boolean isSelectBranch = true;
    private String meeting_rooms_id;
    private String location_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_activity);
        chooseModelList.addAll(CustomerApplication.lListcChooseMode);
        ButterKnife.bind(this);
        app_id = UserManager.getAppId();
        app_secret = UserManager.getAppSecret();
        adapter = new MyAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isSelectBranch) {
                    //选择分店
                    http(chooseModelList.get(position).id);
                    location_id = chooseModelList.get(position).id;
                } else {
                    //选择会议室
//                    is = true;
                    meeting_rooms_id = chooseModelList.get(position).id;
                    tabletInformation(chooseModelList.get(position).name + "电视投屏");
                }
            }
        });
    }

    /**
     * 纪录平板信息
     *
     * @param device_name
     */

    private void tabletInformation(String device_name) {
        Map<String, String> queries = new HashMap<>();
        queries.put("area_id", meeting_rooms_id);
        queries.put("location_id", location_id);
        queries.put("device_name", device_name);
        queries.put("device_id", CustomerApplication.device_id);
        Call<TabletInformationModel> announcementsCall = getKubanApi().sendRecordTabletInformation(app_id, app_secret, queries);
        announcementsCall.enqueue(new Callback<TabletInformationModel>() {
            @Override
            public void onResponse(Call<TabletInformationModel> call, Response<TabletInformationModel> response) {
                if (response != null) {
                    http(app_id, app_secret);
                    Log.e(LOG_TAG, " 记录平板信息成功 ");
                }
            }

            @Override
            public void onFailure(Call<TabletInformationModel> call, Throwable t) {
                Log.e(LOG_TAG, " Throwable  " + t);
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
                if (null != response.body()) {
                    if (!TextUtils.isEmpty(response.body().id)) {
                        TabletInformationModel tabletInformationModel = response.body();
                        cache.put(CustomerApplication.TABLETINFORMATION, tabletInformationModel);
                        Log.e(LOG_TAG, "查询平板信息");
                        UserManager.saveUserObject(app_id, app_secret);
                        StringBuffer url = new StringBuffer();
                        url.append(Constants.URL);
                        url.append(tabletInformationModel.area_id);
                        ActivityManager.startXWalkViewActivity(ChooseActivity.this, new Intent(), url.toString());
                        finish();
                        dismissProgressDialog();
                        return;
                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<TabletInformationModel> call, Throwable t) {
                Log.e("查询该设备是否已存在===========", call + "失败" + t);
            }
        });
    }

    private void http(String location_id) {
        Call<List<ChooseModel>> createSessionCall = getKubanApi().getOptionalMeetingRooms(location_id, app_id, app_secret);
        createSessionCall.enqueue(new Callback<List<ChooseModel>>() {
            @Override
            public void onResponse(Call<List<ChooseModel>> call, Response<List<ChooseModel>> response) {
                if (null != response.body()) {
                    if (response.body().size() > 0) {
                        chooseModelList.clear();
                        chooseModelList.addAll(response.body());
                        adapter.notifyDataSetChanged();
                        isSelectBranch = false;
                    } else {
                        ToastUtils.showShort(ChooseActivity.this, "没有可选会议室");
                    }
                } else if (response.errorBody() != null) {
                    try {
                        String str = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(str);
                        JSONObject jsonObject1 = (JSONObject) jsonObject.get("_error");
                        ToastUtils.customShort(ChooseActivity.this, jsonObject1.getString("message"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ChooseModel>> call, Throwable t) {
                Log.e(LOG_TAG, call + "onFailure" + t);
            }
        });
    }

    @OnClick(R.id.btn_back)
    public void back(View view) {
//        if (is) {
        ActivityManager.startLogInActivity(ChooseActivity.this, new Intent());
        finish();
//        } else {
//            chooseModelList.clear();
//            chooseModelList.addAll(CustomerApplication.lListcChooseMode);
//            adapter.notifyDataSetChanged();
//            is = true;
//        }
    }

    private class MyAdapter extends BaseAdapter {

        public MyAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

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
            chooseName = (TextView) convertView.findViewById(R.id.choose_name);
            chooseName.setText(chooseModelList.get(position).name + "");
            return convertView;
        }
    }
}