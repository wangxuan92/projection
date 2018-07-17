package io.kuban.projection.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;


/**
 * 检查更新
 * Created by wangxuan on 16-4-5.
 */
public class UpdateUtil {


    private static final String TAG = "update";

    public static boolean checkUpdate(Context context) {
        int versionCode = getVersionCode(context);
        String versionName = getVersionName(context);
        String server = ACache.get(context).getAsString(ACache.APPVERSION_PREF);
        Log.e("versionName", versionName + "    " + server);
        String[] server1 = server.split("\\.");
        String[] local = versionName.split("\\.");

        if (server1.length == 3 && local.length == 3) {
            try {
                for (int i = 0; i < 3; i++) {

                    if (Integer.valueOf(server1[i]) > Integer.valueOf(local[i])) {
                        return true;
                    } else if (Integer.valueOf(server1[i]) < Integer.valueOf(local[i])) {
                        return false;
                    }
                }
            } catch (Exception e) {
                //Ignore
            }
        } else if (server1.length < local.length) {
            try {
                for (int i = 0; i < server1.length; i++) {

                    if (Integer.valueOf(server1[i]) > Integer.valueOf(local[i])) {
                        return true;
                    } else if (Integer.valueOf(server1[i]) < Integer.valueOf(local[i])) {
                        return false;
                    }
                }
            } catch (Exception e) {
                //Ignore
            }
        }else if (server1.length > local.length) {
            try {
                for (int i = 0; i < local.length; i++) {

                    if (Integer.valueOf(server1[i]) > Integer.valueOf(local[i])) {
                        return true;
                    } else if (Integer.valueOf(server1[i]) < Integer.valueOf(local[i])) {
                        return false;
                    }
                }
            } catch (Exception e) {
                //Ignore
            }
            return true;
        }


//        if (server1.length == 3 && local.length == 3) {
//            try {
//                if (Integer.valueOf(server1[0]) > Integer.valueOf(local[0])) {
//                    return true;
//                } else if (Integer.valueOf(server1[1]) > Integer.valueOf(local[1])) {
//                    return true;
//                } else if (Integer.valueOf(server1[2]) > Integer.valueOf(local[2])) {
//                    return true;
//                }
//
//            } catch (Exception e) {
//                //Ignore
//            }
//        }
//        for (String d : server1) {
//        }

//        int serverVersionCode = ACache.get(context).getAsInteger(ACache.APPVERSION_PREF);
//        try {
//            result = serverVersionCode > versionCode;
//        } catch (Exception e) {
//            //Ignore
//            L.e(TAG, e.toString());
//        }

        return false;
    }


    public static String getVersionName(Context context) {
        String result = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            result = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getVersionCode(Context context) {
        int result = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            result = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }



}