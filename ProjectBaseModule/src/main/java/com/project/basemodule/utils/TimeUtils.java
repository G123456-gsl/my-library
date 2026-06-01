package com.project.basemodule.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {

    public static long dateToTimestamp(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8")); // 可根据需要调整时区
        try {
            return sdf.parse(dateString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String timestampToDateString(long timestamp) {
        if (timestamp <= 0) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // 注意：这里通常使用系统默认时区，而不是强制 GMT+8
        // sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        try {
            return sdf.format(new Date(timestamp));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 计算出预计到达时间
     *
     * @param remainTimeInSeconds
     * @return
     */
    public static String calculateFormattedArrivalTime(int remainTimeInSeconds) {
        long currentTimeMillis = System.currentTimeMillis();

        long remainTimeMillis = remainTimeInSeconds * 1000L; // 注意用 1000L，防止 int 溢出

        // 3. 计算出预计到达的时间戳
        long arrivalTimeMillis = currentTimeMillis + remainTimeMillis;

        // 4. 创建 SimpleDateFormat 实例来格式化时间
        //    - "a" 代表 上午/下午 标识符
        //    - "h" 代表 12 小时制的小时 (1-12)
        //    - "H" 代表 24 小时制的小时 (0-23)
        //    - "mm" 代表分钟
        SimpleDateFormat sdf = new SimpleDateFormat("a h:mm", Locale.CHINA);
        // 使用 Locale.CHINA 确保 "上午/下午" 是中文，而不是 "AM/PM"

        // 5. 将计算出的时间戳格式化为字符串
        return sdf.format(new Date(arrivalTimeMillis));
    }
}
