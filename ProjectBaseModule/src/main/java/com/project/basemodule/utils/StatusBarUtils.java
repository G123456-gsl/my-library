package com.project.basemodule.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;

/**
 * 沉浸式状态栏
 */
public class StatusBarUtils {

    private static final String TAG = "StatusBarUtils";

    /**
     * 设置沉浸式状态栏，自动判断状态栏文字颜色
     * 支持 Activity 和 Fragment 直接传 this
     *
     * @param object Activity 或 Fragment
     */
    public static void setImmersiveStatusBar(Object object) {
        Activity activity = getActivityFromObject(object);
        if (activity == null || activity.isFinishing()) {
            Log.e(TAG, "无法获取 Activity，请检查传入参数");
            return;
        }

        setStatusBarTransparent(activity);

        // 延迟到视图绘制后再采样顶部颜色，避免 Fragment 尚未布局完成导致取色错误
        View rootView = activity.getWindow().getDecorView();
        rootView.post(new Runnable() {
            @Override
            public void run() {
                if (activity.isFinishing() || rootView.getWidth() == 0) return;

                int topColor = getTopScreenColor(activity);
                boolean isLight = isLightColor(topColor);
                setStatusBarTextColor(activity, isLight);
            }
        });
    }

    /**
     * 从 Activity 或 Fragment 中获取 Activity 实例
     */
    private static Activity getActivityFromObject(Object object) {
        if (object instanceof Activity) {
            return (Activity) object;
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else {
            Log.e(TAG, "传入参数不是 Activity 或 Fragment");
            return null;
        }
    }

    /**
     * 设置状态栏透明
     */
    private static void setStatusBarTransparent(Activity activity) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    /**
     * 获取屏幕顶部区域的颜色
     */
    private static int getTopScreenColor(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        int statusBarHeight = getStatusBarHeight(activity);
        int captureHeight = Math.max(statusBarHeight, 100);
        int captureWidth = decorView.getWidth() > 0 ? decorView.getWidth() : activity.getResources().getDisplayMetrics().widthPixels;

        Bitmap bitmap = Bitmap.createBitmap(captureWidth, captureHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        decorView.draw(canvas);

        int pixel = getBitmapPixel(bitmap, captureWidth / 2, captureHeight / 2);
        bitmap.recycle();
        return pixel;
    }

    /**
     * 获取状态栏高度（px）
     */
    private static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return resourceId > 0 ? context.getResources().getDimensionPixelSize(resourceId) : 0;
    }

    /**
     * 从 Bitmap 中获取指定位置的颜色
     */
    private static int getBitmapPixel(Bitmap bitmap, int x, int y) {
        if (x < 0 || y < 0 || x >= bitmap.getWidth() || y >= bitmap.getHeight()) {
            return Color.BLACK;
        }
        return bitmap.getPixel(x, y);
    }

    /**
     * 判断颜色是否为浅色
     */
    private static boolean isLightColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }

    /**
     * 设置状态栏文字颜色（仅 Android 6.0+ 支持）
     */
    private static void setStatusBarTextColor(Activity activity, boolean isLight) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = activity.getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (isLight) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(flags);
        }
    }

    public static void fitSystemWindow(Activity activity) {
        if (activity == null) return;

        View rootView = activity.findViewById(android.R.id.content);
        if (rootView == null) return;

        // 获取状态栏高度
        int statusBarHeight = getStatusBarHeight(activity);

        // 设置根布局的 paddingTop
        rootView.setPadding(0, statusBarHeight, 0, 0);

        // 如果是 CoordinatorLayout 或其他需要 fitsSystemWindows 的布局
        rootView.setFitsSystemWindows(true);
    }

    public static void fitSystemWindow(Fragment fragment) {
        if (fragment == null || fragment.getActivity() == null) return;
        View rootView = fragment.getView();
        if (rootView == null) return;
        int statusBarHeight = getStatusBarHeight(fragment.getActivity());
        rootView.setPadding(0, statusBarHeight, 0, 0);
        rootView.setFitsSystemWindows(true);
    }
}
