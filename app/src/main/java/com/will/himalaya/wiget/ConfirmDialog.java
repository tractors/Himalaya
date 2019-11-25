package com.will.himalaya.wiget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import com.will.himalaya.R;

public class ConfirmDialog <T> extends Dialog{

    private TextView mCancelSub;
    private TextView mGiveUp;
    private OnDialogActionClickListener mDialogActionClickListener = null;
    private T mFeild = null;
    private TextView mTipsTv;

    public ConfirmDialog(@NonNull Context context) {
        this(context,0);
    }

    public ConfirmDialog(@NonNull Context context, int themeResId) {
        this(context, true,null);
    }

    protected ConfirmDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);
        initView();
        initListener();
    }

    private void initView() {
        mCancelSub = this.findViewById(R.id.dialog_cancel_sub_tv);
        mGiveUp = this.findViewById(R.id.dialog_give_up_tv);
        mTipsTv = this.findViewById(R.id.dialog_tips_tv);
    }

    private void initListener() {
        mCancelSub.setOnClickListener(mListener);
        mGiveUp.setOnClickListener(mListener);
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id){
                case R.id.dialog_cancel_sub_tv:
                    if (mDialogActionClickListener != null) {
                        mDialogActionClickListener.onCancelSubClick(mFeild);
                        dismiss();
                    }
                    break;
                case R.id.dialog_give_up_tv:
                    if (mDialogActionClickListener != null) {
                        mDialogActionClickListener.onGiveUpClick();
                        dismiss();
                    }
                    break;
            }
        }
    };

    public void setOnDialogActionClickListener(OnDialogActionClickListener clickListener, T data){
        this.mDialogActionClickListener = clickListener;
        this.mFeild = data;
    }

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

    public interface OnDialogActionClickListener<T>{
        void onCancelSubClick(T data);

        void onGiveUpClick();
    }

}
