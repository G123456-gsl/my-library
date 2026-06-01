package com.project.basemodule;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.project.basemodule.R;

/**
 * 自定义进度条
 */
public class CustomProgressBar extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private RectF backgroundRect;
    private RectF progressRect;

    //定义自定义属性变量
    private int progressColor = 0xFF1E40AF; // 默认进度条颜色
    private int backgroundColor = 0xFFE5E7EB; // 默认背景颜色
    private float progressHeight = 15f; // 默认进度条高度
    private float cornerRadius = 10f; // 默认圆角半径

    private float progress = 0f;

    public CustomProgressBar(Context context) {
        super(context);
        init(null);
    }

    public CustomProgressBar(Context context,AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomProgressBar(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CustomProgressBar);

            progressColor = ta.getColor(R.styleable.CustomProgressBar_progressColor, progressColor);
            backgroundColor = ta.getColor(R.styleable.CustomProgressBar_backgroundColor, backgroundColor);

            progressHeight = ta.getDimension(R.styleable.CustomProgressBar_progressHeight, progressHeight);
            cornerRadius = ta.getDimension(R.styleable.CustomProgressBar_cornerRadius, cornerRadius);

            ta.recycle();
        }

        // 初始化画笔
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.FILL);

        // 初始化矩形
        backgroundRect = new RectF();
        progressRect = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 宽度尽可能大，高度使用我们定义的 progressHeight
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) progressHeight;

        // 考虑内边距 (padding)
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        width = width - paddingLeft - paddingRight;
        height = height - paddingTop - paddingBottom;

        // 更新矩形的尺寸和位置
        backgroundRect.set(paddingLeft, paddingTop, paddingLeft + width, paddingTop + height);
        progressRect.set(paddingLeft, paddingTop, paddingLeft + width, paddingTop + height);

        // 设置最终测量的尺寸
        setMeasuredDimension(width + paddingLeft + paddingRight, height + paddingTop + paddingBottom);
    }

    //重写 onDraw 来绘制进度条
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制背景
        canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, backgroundPaint);

        // 计算进度条的宽度
        float progressWidth = backgroundRect.width() * (progress / 100f);

        // 更新进度矩形的右边界
        progressRect.right = backgroundRect.left + progressWidth;

        // 绘制进度
        canvas.drawRoundRect(progressRect, cornerRadius, cornerRadius, progressPaint);
    }

    public void setProgress(float progress) {
        this.progress = Math.max(0, Math.min(100, progress));
        invalidate();
    }

    public float getProgress() {
        return progress;
    }
}
