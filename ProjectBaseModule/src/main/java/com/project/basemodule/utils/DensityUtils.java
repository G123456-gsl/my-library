package com.project.basemodule.utils;

import android.content.Context;
import android.util.TypedValue;

public class DensityUtils {

    /**
     * dp 转 px
     * @param context 上下文
     * @param dpValue dp 值
     * @return px 值
     */
    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpValue,
                context.getResources().getDisplayMetrics()
        );
    }

    /**
     * 米换算公里
     */
    public static String formatDistance(int meters) {
        if (meters >= 1000) {
            return String.format("%.1f 公里", meters / 1000.0);
        } else {
            return String.format("%d 米", meters);
        }
    }
}
