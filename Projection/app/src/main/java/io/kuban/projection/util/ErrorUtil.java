package io.kuban.projection.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.kuban.projection.base.ActivityManager;
import retrofit2.Response;

/**
 * Created by wangxuan on 17/2/8.
 */

public class ErrorUtil {
    private static final Handler MAIN_LOOPER_HANDLER = new Handler(Looper.getMainLooper());

    public static void handleError(final Activity activity, final Response<?> response) {
        MAIN_LOOPER_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (response.code() == 401) {
                    EventBus.getDefault().post(new HttpUnauthorizedEvent(activity));
                    return;
                }
                try {
                    String errorBody = response.errorBody().string();
                    JSONObject jsonObject = new JSONObject(errorBody);
                    if (jsonObject.toString().charAt(2) == '_') {
                        JSONObject errorObject = jsonObject.getJSONObject("_error");
                        if (errorObject != null) {
                            String message = errorObject.getString("message");
                            if (!TextUtils.isEmpty(message)) {
                                ToastUtils.showShort(activity, message);
                            }
                        } else {
                            ToastUtils.showShort(activity, "服务器发生错误，请稍后再试");
                        }
                    } else if (jsonObject.toString().charAt(2) == 'p') {
                        String phone_num = jsonObject.getString("phone_num");
                        if (!TextUtils.isEmpty(phone_num)) {
                            ToastUtils.customShort(activity, phone_num);
                        }
                    } else {
                        String message = jsonObject.getString("message");
                        if (!TextUtils.isEmpty(message)) {
                            ToastUtils.showShort(activity, message);
                            return;
                        }
                    }
                } catch (Exception e) {
                    ToastUtils.showShort(activity, "服务器发生错误，请稍后再试");
                }
            }
        });

    }

    public static void handleError1(final Activity activity, final Response<?> response) {
        MAIN_LOOPER_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        String errorBody = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorBody);
                        if (jsonObject.toString().charAt(2) == '_') {
                            JSONObject errorObject = jsonObject.getJSONObject("_error");
                            if (errorObject != null) {
                                String message = errorObject.getString("message");
                                if (!TextUtils.isEmpty(message)) {
                                    ToastUtils.showShort(activity, message);
                                }
                            } else {
                                ToastUtils.showShort(activity, "服务器发生错误，请稍后再试");
                            }
                        } else if (jsonObject.toString().charAt(2) == 'p') {
                            String phone_num = jsonObject.getString("phone_num");
                            if (!TextUtils.isEmpty(phone_num)) {
                                ToastUtils.showShort(activity, phone_num);
                            } else {
                                ToastUtils.showShort(activity, "服务器发生错误，请稍后再试");
                            }
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        ToastUtils.showShort(activity, "服务器发生错误，请稍后再试");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                } catch (Exception e) {
                    String errorBody = null;

                }
            }
        });
    }


    public static void handleError(final Activity activity, final Throwable t) {
        MAIN_LOOPER_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (t instanceof IOException) {
//                    Tips.showShort(activity, "网络尚未连接", true);
                    ToastUtils.showShort(activity, "网络连接超时");
                } else {
//                    Tips.showShort(activity, "验证码获取失败, 请重试", true);
                    ToastUtils.showShort(activity, "网络获取失败, 请重试");
                }
            }
        });
    }

    public static void handleError1(final Activity activity, final Throwable t) {
        MAIN_LOOPER_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (t instanceof IOException) {
//                    Tips.showShort(activity, "网络尚未连接", true);
                    ToastUtils.showShort(activity, "网络尚未连接");
                } else {
//                    Tips.showShort(activity, "验证码获取失败, 请重试", true);
                    ToastUtils.showShort(activity, "验证码获取失败, 请重试");
                }
            }
        });
    }

    // Authorization failure
    public static class HttpUnauthorizedEvent {
        public HttpUnauthorizedEvent(Activity activity) {
            UserManager.getRemove();
            ActivityManager.startLogInActivity(activity, new Intent());
        }
    }
}
