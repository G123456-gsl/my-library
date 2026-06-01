package com.project.basemodule.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.project.basemodule.R;

/**
 * 带点击缩放动画的自定义 TextView
 * 仅保留 TextView 核心功能 + 按下缩小/抬起恢复的动效
 */
public class PressDownTextView extends AppCompatTextView {

    private float scaleFactor = 0.7f;    // 按下缩放比例
    private int animDuration = 150;      // 动画时长（毫秒）
    private boolean animationEnabled = true; // 是否启用动画

    // 构造函数
    public PressDownTextView(Context context) {
        super(context);
        init(null);
    }

    public PressDownTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PressDownTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /**
     * 初始化：读取自定义属性 + 设置触摸监听
     */
    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PressDownTextView);
            scaleFactor = typedArray.getFloat(R.styleable.PressDownTextView_scaleFactor, scaleFactor);
            animDuration = typedArray.getInt(R.styleable.PressDownTextView_animDuration, animDuration);
            animationEnabled = typedArray.getBoolean(R.styleable.PressDownTextView_animationEnabled, animationEnabled);
            typedArray.recycle();
        }

        // 设置触摸监听（核心：处理缩放动画）
        setOnTouchListener(touchListener);
    }

    /**
     * 触摸监听：处理按下/抬起/取消的缩放动画
     */
    private final OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!animationEnabled) return false;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ValueAnimator downAnim = ValueAnimator.ofFloat(1f, scaleFactor);
                    downAnim.setDuration(animDuration);
                    downAnim.addUpdateListener(animation -> {
                        float value = (float) animation.getAnimatedValue();
                        v.setScaleX(value);
                        v.setScaleY(value);
                    });
                    downAnim.start();
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    ValueAnimator upAnim = ValueAnimator.ofFloat(scaleFactor, 1f);
                    upAnim.setDuration(animDuration);
                    upAnim.addUpdateListener(animation -> {
                        float value = (float) animation.getAnimatedValue();
                        v.setScaleX(value);
                        v.setScaleY(value);
                    });
                    upAnim.start();
                    break;
            }
            return false;
        }
    };

    /**
     * 设置是否启用点击动画
     */
    public void setAnimationEnabled(boolean enabled) {
        this.animationEnabled = enabled;
    }

    /**
     * 设置按下缩放比例（0~1 之间，比如 0.8 表示缩小到80%）
     */
    public void setScaleFactor(float scaleFactor) {
        if (scaleFactor > 0 && scaleFactor <= 1) {
            this.scaleFactor = scaleFactor;
        }
    }

    /**
     * 设置动画时长（毫秒）
     */
    public void setAnimDuration(int duration) {
        if (duration >= 0) {
            this.animDuration = duration;
        }
    }

    // ------------------- 获取参数方法 -------------------
    public boolean isAnimationEnabled() {
        return animationEnabled;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public int getAnimDuration() {
        return animDuration;
    }
}