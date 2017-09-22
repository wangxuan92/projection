package io.kuban.projection.util;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;

import io.kuban.projection.base.Constants;

/**
 * Created by wangxuan on 17/5/2.
 */

public class Utils {
    //防止控件被重复点击
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * @param type
     * @return 以MB为单位
     */
    public static String memorySize(String type) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize;
        long totalBlocks;
        long availableBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
            availableBlocks = stat.getAvailableBlocks();
        }
        String totalText = "";
        if (type.equals("total")) {
            totalText = ((blockSize * totalBlocks) / (1024 * 1024)) + "";
        } else if (type.equals("available")) {
            totalText = ((blockSize * availableBlocks) / (1024 * 1024)) + "";
        }
        return totalText;
    }

    public static String saveUrl(String server, String area_id) {
        StringBuffer urlStr = new StringBuffer();
        urlStr.append("http://");
        urlStr.append(server);
        urlStr.append("/projector/?area_id=");
        urlStr.append(area_id);
        Log.e("Utils", "保存url：     " + urlStr.toString());
        return urlStr.toString();
    }
}
