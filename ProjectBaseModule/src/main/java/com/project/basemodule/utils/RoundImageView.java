package com.project.basemodule.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.project.basemodule.R;

public class RoundImageView extends AppCompatImageView {

    private int cornerSize;//总的圆角大小
    private int topLeftSize;
    private int topRightSize;
    private int bottomLeftSize;
    private int bottomRightSize;
    private Context context;

    public void setCornerSize(int cornerSize) {
        this.cornerSize = cornerSize;
    }

    public int getTopLeftSize() {
        return topLeftSize;
    }

    public void setTopLeftSize(int topLeftSize) {
        this.topLeftSize = topLeftSize;
    }

    public int getTopRightSize() {
        return topRightSize;
    }

    public void setTopRightSize(int topRightSize) {
        this.topRightSize = topRightSize;
    }

    public int getBottomLeftSize() {
        return bottomLeftSize;
    }

    public void setBottomLeftSize(int bottomLeftSize) {
        this.bottomLeftSize = bottomLeftSize;
    }

    public int getBottomRightSize() {
        return bottomRightSize;
    }

    public void setBottomRightSize(int bottomRightSize) {
        this.bottomRightSize = bottomRightSize;
    }

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerImageView, defStyle, 0);
        cornerSize = a.getInt(R.styleable.RoundCornerImageView_corner_size, 0);
        topLeftSize = a.getInt(R.styleable.RoundCornerImageView_top_left_size, 0);
        topRightSize = a.getInt(R.styleable.RoundCornerImageView_top_right_size, 0);
        bottomLeftSize = a.getInt(R.styleable.RoundCornerImageView_bottom_left_size, 0);
        bottomRightSize = a.getInt(R.styleable.RoundCornerImageView_bottom_right_size, 0);
        setScaleType(ScaleType.FIT_XY);
    }

    public int getCornerSize() {
        return cornerSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        int w = getWidth();
        int h = getHeight();
        //这里对path添加一个圆角区域，这里一般需要将dp转换为pixel
        if (cornerSize != 0) {
            path.addRoundRect(new RectF(0, 0, w, h), DensityUtils.dp2px(context, cornerSize), DensityUtils.dp2px(context, cornerSize), Path.Direction.CW);
        } else {
            float floats[] = {DensityUtils.dp2px(context, topLeftSize), DensityUtils.dp2px(context, topLeftSize),
                    DensityUtils.dp2px(context, topRightSize), DensityUtils.dp2px(context, topRightSize),
                    DensityUtils.dp2px(context, bottomRightSize), DensityUtils.dp2px(context, bottomRightSize),
                    DensityUtils.dp2px(context, bottomLeftSize), DensityUtils.dp2px(context, bottomLeftSize)};
            path.addRoundRect(new RectF(0, 0, w, h), floats, Path.Direction.CW);
        }

        canvas.clipPath(path);//将Canvas按照上面的圆角区域截取
        super.onDraw(canvas);
    }

}
