package com.project.basemodule.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.project.basemodule.R;

/**
 * 一个功能强大的自定义 Toast 工具类。
 * <p>
 * 特性：
 * 1. 支持完全自定义布局。
 * 2. 内置成功、错误、信息、警告等预设样式。
 * 3. 支持自定义显示位置（上、中、下）。
 * 4. 采用 Builder 模式，链式调用，灵活易用。
 * 5. 线程安全，可在任意线程调用。
 * 6. 自动取消上一个 Toast，避免连续显示。
 */
public class ToastUtils {

    private static final String TAG = ToastUtils.class.getSimpleName();
    private static ToastUtils sInstance;

    private final Application mApplication;
    private Toast mToast;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    // 私有化构造函数
    private ToastUtils(Application application) {
        this.mApplication = application;
    }

    /**
     * 初始化，必须在 MyApplication 中调用
     * @param application Application context
     */
    public static void init(Application application) {
        if (sInstance == null) {
            sInstance = new ToastUtils(application);
        }
    }

    /**
     * 获取单例实例
     * @return ToastUtils instance
     */
    public static ToastUtils getInstance() {
        if (sInstance == null) {
            // 如果没有初始化，抛出异常，提醒开发者必须在 Application 中调用 init()
            throw new IllegalStateException("ToastUtils must be init in Application first!");
        }
        return sInstance;
    }

    // --- 便捷方法 (使用 Application Context) ---

    public static void show(int resId) {
        getInstance().createBuilder().setMessage(resId).show();
    }

    public static void show(String message) {
        getInstance().createBuilder().setMessage(message).setLayoutRes(R.layout.toast_custom_layout).show();
    }

    private static void success(int resId) {
        getInstance().createBuilder().setMessage(resId).setSuccessStyle().show();
    }

    private static void success(String message) {
        getInstance().createBuilder().setMessage(message).setSuccessStyle().show();
    }

    public Builder createBuilder() {
        return new Builder();
    }

    public class Builder {
        private String message;
        private int messageResId;
        private int layoutResId = -1;
        private int gravity = Gravity.CENTER;
        private int xOffset = 0;
        private int yOffset = (int) (64 * mApplication.getResources().getDisplayMetrics().density + 0.5); // 默认距离底部64dp
        private int bgColor;
        private int textColor;
        private int iconResId;

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(int resId) {
            this.messageResId = resId;
            return this;
        }

        public Builder setLayoutRes(int layoutResId) {
            this.layoutResId = layoutResId;
            return this;
        }

        public Builder setGravity(int gravity, int xOffset, int yOffset) {
            this.gravity = gravity;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            return this;
        }

        public Builder setBackgroundColor(int color) {
            this.bgColor = color;
            return this;
        }

        public Builder setTextColor(int color) {
            this.textColor = color;
            return this;
        }

        public Builder setIcon(int iconResId) {
            this.iconResId = iconResId;
            return this;
        }

        // --- 预设样式 ---
        public Builder setSuccessStyle() {
            this.layoutResId = R.layout.toast_custom_layout;
            this.bgColor = ContextCompat.getColor(mApplication, android.R.color.holo_green_light);
            this.iconResId = R.drawable.icon_home_nor;
            this.textColor = ContextCompat.getColor(mApplication, android.R.color.white);
            return this;
        }

        @SuppressLint("ShowToast")
        public void show() {
            // 确保在主线程执行
            mHandler.post(() -> {
                // 取消上一个 Toast
                if (mToast != null) {
                    mToast.cancel();
                }

                // 获取最终要显示的消息
                String finalMessage = message != null ? message : mApplication.getString(messageResId);

                // 如果设置了自定义布局
                if (layoutResId != -1) {
                    // 关键：使用 Application Context 来加载布局
                    View layout = LayoutInflater.from(mApplication).inflate(layoutResId, null);
                    LinearLayout root = layout.findViewById(R.id.toast_custom_root);
                    TextView tvMessage = layout.findViewById(R.id.toast_custom_message);
                    ImageView ivIcon = layout.findViewById(R.id.toast_custom_icon);

                    tvMessage.setText(finalMessage);
                    if (bgColor != 0) root.setBackgroundColor(bgColor);
                    if (textColor != 0) tvMessage.setTextColor(textColor);
                    if (iconResId != 0) {
                        ivIcon.setImageResource(iconResId);
                        ivIcon.setVisibility(View.VISIBLE);
                    } else {
                        ivIcon.setVisibility(View.GONE);
                    }

                    // 关键：使用 Application Context 创建 Toast
                    mToast = new Toast(mApplication);
                    mToast.setView(layout);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                    mToast.setGravity(gravity, xOffset, yOffset);
                } else {
                    // 使用默认样式
                    // 关键：使用 Application Context 创建 Toast
                    mToast = Toast.makeText(mApplication, finalMessage, Toast.LENGTH_SHORT);
                    mToast.setGravity(gravity, xOffset, yOffset);
                }

                mToast.show();
            });
        }
    }
}


