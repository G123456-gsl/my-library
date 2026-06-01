package com.project.basemodule.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.project.basemodule.R;

public class CustomDialog extends Dialog {

    private TextView tvCustomTitle, tvCustomContent, tvCustomConfirm, tvCustomCancel;
    private View viewLine,viewTopLine;

    private OnConfirmListener onConfirmListener;
    private OnCancelListener onCancelListener;
    private int dialogType;

    public CustomDialog(Context context) {
        super(context, R.style.CustomDialog);
    }

    public interface OnConfirmListener {
        void onConfirm();
    }

    public interface OnCancelListener {
        void onCancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom_layout);
        tvCustomContent = findViewById(R.id.tv_custom_content);
        tvCustomTitle = findViewById(R.id.tv_custom_title);
        tvCustomConfirm = findViewById(R.id.tv_custom_confirm);
        tvCustomCancel = findViewById(R.id.tv_custom_cancel);
        viewLine = findViewById(R.id.view_line);
        viewTopLine = findViewById(R.id.view_top_line);

        tvCustomConfirm.setOnClickListener(v -> {
            if (onConfirmListener != null) {
                onConfirmListener.onConfirm();
            }
        });
        tvCustomCancel.setOnClickListener(v -> {
            if (onCancelListener != null) {
                onCancelListener.onCancel();
            }
        });
    }


    //设置title
    public void setTvCustomTitle(String title) {
        tvCustomTitle.setText(title);
    }

    //设置内容
    public void setTvCustomContent(String content) {
        tvCustomContent.setText(content);
    }

    //确认按钮文本修改
    public void setTvCustomConfirm(String confirm){
        tvCustomConfirm.setText(confirm);
    }

    //隐藏取消按钮
    public void hideTvCustomCancel(){
        tvCustomCancel.setVisibility(View.GONE);
        viewLine.setVisibility(View.GONE);
    }

    //隐藏所有按钮
    public void hideALLCustomCancel(){
        tvCustomCancel.setVisibility(View.GONE);
        viewLine.setVisibility(View.GONE);
        tvCustomConfirm.setVisibility(View.GONE);
        viewTopLine.setVisibility(View.GONE);
    }

    public void setDialogType(int type) {
        this.dialogType = type;
    }

    public int getDialogType() {
        return dialogType;
    }


    public void setOnConfirmListener(OnConfirmListener listener) {
        this.onConfirmListener = listener;
    }

    public void setOnCancelListener(OnCancelListener listener) {
        this.onCancelListener = listener;
    }

}
