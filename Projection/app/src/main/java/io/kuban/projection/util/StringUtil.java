package io.kuban.projection.util;


import android.content.res.Resources;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.kuban.projection.CustomerApplication;

/**
 * Created by wangxuan on 16-4-5.
 */
public class StringUtil {

    public static final String CN_COMMA = "，"; //中文逗号
    public static final String EN_COMMA = ","; //英文逗号
    public static final String SPACE = " ";
    public static final String POINT_CENTER = "·";//英文姓氏中间点
    /**
     * 一分钟的毫秒值，用于判断上次的发布时间
     */
    public static final long ONE_MINUTE = 60 * 1000;

    /**
     * 一小时的毫秒值，用于判断上次的发布时间
     */
    public static final long ONE_HOUR = 60 * ONE_MINUTE;

    /**
     * 一天的毫秒值，用于判断上次的发布时间
     */
    public static final long ONE_DAY = 24 * ONE_HOUR;

    /**
     * 一月的毫秒值，用于判断上次的发布时间
     */
    public static final long ONE_MONTH = 30 * ONE_DAY;

    /**
     * 一年的毫秒值，用于判断上次的发布时间
     */
    public static final long ONE_YEAR = 12 * ONE_MONTH;


    public static String idgui(String s, int num) throws Exception {
        int changdu = s.getBytes("UTF-8").length;
        if (changdu > num) {
            s = s.substring(0, s.length() - 1);
            s = idgui(s, num);
        }
        return s;
    }

    /**
     * 毫秒值转换成对应String
     *
     * @param time
     * @return
     */
    public static String timeToString(long time) {

        long currentTime = System.currentTimeMillis();//获得当前的时间
        long timePassed = currentTime - time;
        if (timePassed < ONE_MINUTE) {
            return "刚刚";
        } else if (timePassed < ONE_HOUR) {
            return timePassed / ONE_MINUTE + "分钟前";
        } else if (timePassed > ONE_HOUR && timePassed < ONE_DAY) {
            return timePassed / ONE_HOUR + "小时前";
        } else if (timePassed > ONE_DAY && timePassed < 2 * ONE_DAY) {
            Date date = new Date(time);
            int hour = date.getHours();
            int m = date.getMinutes();
            return "昨天 " + hour + ":" + m;
        } else {
            Date date = new Date(time);
            Date date1 = new Date();

            if (date.getYear() != date1.getYear()) {
                SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
                return format.format(date);
            } else {
                SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
                return format.format(date);
            }

        }
    }

    public static String listToString(List list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (i < list.size() - 1) {
                    sb.append(list.get(i) + ",");
                } else {
                    sb.append(list.get(i));
                }
            }
        }
        return sb.toString();
    }

    public static String converToString(String[] ig) {
        String str = "";
        if (ig != null && ig.length > 0) {
            for (int i = 0; i < ig.length; i++) {
                str += ig[i] + ",";
            }
        }
        str = str.substring(0, str.length() - 1);
        return str;
    }


    public static int stringToInt(String s) {
        int result = -1;
        try {
            result = Integer.valueOf(s);
        } catch (Exception e) {
            //Ignore
        }
        return result;
    }

    public static String[] array_unique(String[] a) {
        List<String> list = new LinkedList<String>();
        for (int i = 0; i < a.length; i++) {
            if (!list.contains(a[i])) {
                list.add(a[i]);
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

    /**
     * 替换关键字  实现关键字变色
     *
     * @return
     */
    public static String replaceKeyWord(String key, String text, String color) {
        //tv.setText(Html.fromHtml("我是<font color=blue>danyijiangnan</font>"));
        if (key == null) {
            return text;
        }
        String newString = text.replaceAll(key, "<font color=" + color + ">" + key + "</font>");
        return newString;
    }

    public static String getString(int stringId, Object... formatArgs) {
        Resources res = CustomerApplication.context.getResources();
        return res.getString(stringId, formatArgs);
    }
}
