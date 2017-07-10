package io.kuban.projection.base;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Created by wang on 2016/8/9.
 */

public class CommonHeaderInterceptor implements Interceptor {

    //    private UserManager mUserManager;
//    private SpacesModel spacesModel;
    private Context context;

    public CommonHeaderInterceptor(Context context) {
//        mUserManager = UserManager.getInstance();
        this.context = context;
    }

    public CommonHeaderInterceptor() {
//        mUserManager = UserManager.getInstance();
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        String space_id = "0";
        String location_id = "0";

//        spacesModel = (SpacesModel) ACache.get(context).getAsObject(CustomerApplication.SPACE);
//        if (spacesModel != null) {
//            space_id = spacesModel.id;
//        }

        Request originRequest = chain.request();
        Request.Builder builder = originRequest
                .newBuilder()
                .addHeader("Accept", "application/json");
//                .addHeader("X-space-id", space_id)
//                .addHeader("X-location-id", CustomerApplication.X_LOCATION_ID);
//        if (!TextUtils.isEmpty(mUserManager.getUserToken())) {
//            builder.addHeader("Authorization", " Bearer " + mUserManager.getUserToken());
//        }
//        builder.addHeader("Authorization", " Bearer " + TEST_TOKEN);

        Request newRequest = builder.build();
        return chain.proceed(newRequest);
    }
}
