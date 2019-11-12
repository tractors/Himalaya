package com.will.himalaya.wiget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

public class DetailListItemDecoration extends RecyclerView.ItemDecoration {

    private int mColor;
    private int left;
    private int right;
    private int top;
    private int bottom;
    private int topBottom;
    //color的传入方式是resouce.getcolor
    private Drawable mDivider;

    public DetailListItemDecoration(int leftRight,int topBottom,@ColorInt int color){
        this.mColor = color;
        this.left = leftRight;
        this.right = leftRight;
        this.top = topBottom;
        this.bottom = topBottom;
        this.topBottom = topBottom;
        if (0 != mColor) {
            mDivider = new ColorDrawable(mColor);
        }
    }


    public DetailListItemDecoration(int left,int right,int topBottom,@ColorInt int color){
        this.mColor = color;
        this.left = left;
        this.right = right;
        this.top = topBottom;
        this.bottom = topBottom;
        this.topBottom = topBottom;
        if (0 != mColor) {
            mDivider = new ColorDrawable(mColor);
        }
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        //竖直方向的
        if (layoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
            //最后一项需要 bottom
            if (parent.getChildAdapterPosition(view) == layoutManager.getItemCount() - 1) {
                outRect.bottom = bottom;
            }
            outRect.top = top;
            outRect.left = left;
            outRect.right = right;
        } else {
            //最后一项需要right
            if (parent.getChildAdapterPosition(view) == layoutManager.getItemCount() - 1) {
                outRect.right = right;
            }
            outRect.top = top;
            outRect.left = left;
            outRect.bottom = bottom;
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        //没有子view或者没有没有颜色直接return
        if (mDivider == null || layoutManager.getChildCount() == 0) {
            return;
        }
        int left;
        int right;
        int top;
        int bottom;

        final int childCount = parent.getChildCount();
        if (layoutManager.getOrientation() == GridLayoutManager.VERTICAL) {
            for (int i = 0; i < childCount - 1; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                //将有颜色的分割线处于中间位置
                final float center = (layoutManager.getTopDecorationHeight(child) + 1 - this.topBottom) / 2;
                //计算下边的
                left = layoutManager.getLeftDecorationWidth(child);
                right = parent.getWidth() - layoutManager.getLeftDecorationWidth(child);
                top = (int) (child.getBottom() + center);
                bottom = top + this.topBottom;
                mDivider.setBounds(left+UIUtil.dip2px(parent.getContext(),18), top, right-UIUtil.dip2px(parent.getContext(),20), bottom);
                mDivider.draw(c);
            }
        }
    }
}
