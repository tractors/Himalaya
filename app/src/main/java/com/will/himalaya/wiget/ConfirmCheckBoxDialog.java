package com.will.himalaya.wiget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.will.himalaya.R;

public class ConfirmCheckBoxDialog<T> extends Dialog{

    private TextView mCancel;
    private TextView mConfirm;
    private OnDialogActionClickListener mDialogActionClickListener = null;
    private T mField = null;
    private TextView mTipsTv;
    //确认按钮的text
    private String mCancelSubTips = null;
    //放弃按钮的text
    private String mGiveUpTips = null;
    //设置提示内容
    private String mTipsTvContent = null;
    private OnDialogClickListener mDialogClickListener = null;
    private T field = null;
    private CheckBox mCheckBox;

    public ConfirmCheckBoxDialog(@NonNull Context context) {
        this(context,0);
    }

    public ConfirmCheckBoxDialog(@NonNull Context context, int themeResId) {
        this(context, true,null);
    }

    protected ConfirmCheckBoxDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_box_confirm);
        initView();
        initListener();
    }

    private void initView() {
        mCancel = this.findViewById(R.id.dialog_check_box_cancel);
        mConfirm = this.findViewById(R.id.dialog_check_box_confirm);
        mTipsTv = this.findViewById(R.id.dialog_tips_text);
        mCheckBox = this.findViewById(R.id.dialog_check_box);
    }

    private void initListener() {
        mCancel.setOnClickListener(mListener);
        mConfirm.setOnClickListener(mListener);
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id){
                case R.id.dialog_check_box_cancel:
                    if (mDialogActionClickListener != null) {
                        mDialogActionClickListener.onCancelSubClick(mField);
                        dismiss();
                    }

                    if (mDialogClickListener != null){
                        mDialogClickListener.onCancelSubClick();
                        dismiss();
                    }
                    break;
                case R.id.dialog_check_box_confirm:
                    boolean isChecked = mCheckBox.isChecked();
                    if (mDialogActionClickListener != null) {
                        mDialogActionClickListener.onConfirmClick();
                        dismiss();
                    }

                    if (mDialogClickListener != null) {
                        mDialogClickListener.onConfirmClick(field,isChecked);
                        dismiss();
                    }
                    break;
            }
        }
    };

    public void setTips(String tips){
        if (mTipsTv != null) {

            mTipsTv.setText(tips);
        }
    }

    public void setTipsResId(@StringRes int strId){
        if (mTipsTv != null) {

            mTipsTv.setText(strId);
        }
    }

    public void setOnDialogActionClickListener(OnDialogActionClickListener clickListener, T data){
        this.mDialogActionClickListener = clickListener;
        this.mField = data;
    }

    public interface OnDialogActionClickListener<T>{
        void onCancelSubClick(T data);

        void onConfirmClick();
    }

    public void setOnDialogClickListener(OnDialogClickListener clickListener,T data){
        this.mDialogClickListener = clickListener;
        this.field = data;
    }

    public interface OnDialogClickListener<T>{
        void onCancelSubClick();

        void onConfirmClick(T data,boolean isChecked);
    }

}
