package com.will.himalaya.wiget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.will.himalaya.R;

@SuppressLint("AppCompatCustomView")
public class LoadingView extends ImageView {

    //旋转的角度
    private int rotatDegree = 0;
    private boolean mNeedRotate = false;

    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //设置图片
        setImageResource(R.mipmap.loading);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * 第一个参数是旋转角度
         * 第二个参数是旋转x坐标
         * 第二个参数是旋转的y坐标
         */
        canvas.rotate(rotatDegree,getWidth()/2,getHeight()/2);
        super.onDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //绑定到window的时候
        mNeedRotate = true;
        post(new Runnable() {
            @Override
            public void run() {
                rotatDegree += 30;
                rotatDegree = rotatDegree <= 360 ? rotatDegree : rotatDegree % 360;
                invalidate();
                //判断是否继续旋转
                if (mNeedRotate) {
                    postDelayed(this,150);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //window中解绑了
        mNeedRotate = false;
    }
}
