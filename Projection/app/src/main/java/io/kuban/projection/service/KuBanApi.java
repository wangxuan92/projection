package io.kuban.projection.service;

import java.util.List;
import java.util.Map;

import io.kuban.projection.model.ChooseModel;
import io.kuban.projection.model.PadsModel;
import io.kuban.projection.model.TabletInformationModel;
import io.kuban.projection.model.ToKenModel;
import io.kuban.projection.model.UserModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by wang on 2016/8/3.
 */


public interface KuBanApi {
    /**
     * ----------获取version信息------------------------------
     */
    @GET("version")
    Call<ResponseBody> getVersion();

    /**
     * ----------获取configuration信息-----------------------------
     */
    @GET("configuration")
    Call<ResponseBody> getConfiguration();

    /**
     * ----------发送登录短信验证码-----------------------------
     */
    @POST("sessions/send_sms_code")
    Call<ResponseBody> sendSmsCode(@Query("phone_num") String phone_num);

    /**
     * ----------登录-----------------------------
     */
    @POST("sessions")
    Call<UserModel> sendSessions(@Query("phone_num") String phone_num, @Query("login_sms_code") String login_sms_code);


    /**
     * ------------可选分店---------------------------
     */
    @GET("meeting_screens/locations")
    Call<List<ChooseModel>> getLocations(@Query("app_id") String app_id, @Query("app_secret") String app_secret);

    /**
     * ----------记录平板信息-----------------------------
     */
    @POST("meeting_screens/device_save")
    Call<TabletInformationModel> sendRecordTabletInformation(@Query("app_id") String app_id, @Query("app_secret") String app_secret, @QueryMap Map<String, String> queries);

    /**
     * ------------查询平板的信息---------------------------
     */
    @GET("meeting_screens/{id}/device_info")
    Call<TabletInformationModel> getTabletInformation(@Path("id") String id, @Query("app_id") String app_id, @Query("app_secret") String app_secret);

    /**
     * ------------更新平板信息---------------------------
     */
    @PUT("meeting_screens/{id}/device_update")
    Call<TabletInformationModel> updateTabletInformation(@Path("id") String id, @Query("app_id") String app_id, @Query("app_secret") String app_secret, @QueryMap Map<String, String> queries);

    /**
     * ------------可选会议室---------------------------
     */
    @GET("meeting_screens/meeting_rooms")
    Call<List<ChooseModel>> getOptionalMeetingRooms(@Query("location_id") String location_id, @Query("app_id") String app_id, @Query("app_secret") String app_secret);

    /**
     * ----------注册pad-----------------------------
     */
    @POST("pads/register")
    Call<ToKenModel> postRegister(@Body Map<String, String> queries);

    /**
     * ----------获取pad绑定信息-----------------------------
     */
    @GET("pads/{id}")
    Call<PadsModel> getPads(@Path("id") String id);


}
