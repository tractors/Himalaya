package com.will.himalaya.wiget.spacesitemdecoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

public class StaggeredGridEntrust extends SpacesItemDecorationEntrust{
    public StaggeredGridEntrust(int leftRight, int topBottom, int mColor) {
        super(leftRight, topBottom, mColor);

    }

    @Override
    void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        // 因为排列的不确定性，暂时没找到比较好的处理方式

    }

    @Override
    void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) parent.getLayoutManager();
        final StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        final int childPosition = parent.getChildAdapterPosition(view);
        final int spanCount = layoutManager.getSpanCount();
        final int spanSize = lp.isFullSpan() ? layoutManager.getSpanCount() : 1;

        if (layoutManager.getOrientation() == GridLayoutManager.VERTICAL) {
            if (getSpanGroupIndex(childPosition, spanCount, spanSize) == 0) {//第一排的需要上面
                outRect.top = topBottom;
            }
            outRect.bottom = topBottom;
            //这里忽略和合并项的问题，只考虑占满和单一的问题
            if (lp.isFullSpan()) {//占满
                outRect.left = leftRight;
                outRect.right = leftRight;
            } else {
                outRect.left = (int) (((float) (spanCount - lp.getSpanIndex())) / spanCount * leftRight);
                outRect.right = (int) (((float) leftRight * (spanCount + 1) / spanCount) - outRect.left);
            }
        } else {
            if (getSpanGroupIndex(childPosition, spanCount, spanSize) == 0) {//第一排的需要left
                outRect.left = leftRight;
            }
            outRect.right = leftRight;
            //这里忽略和合并项的问题，只考虑占满和单一的问题
            if (lp.isFullSpan()) {//占满
                outRect.top = topBottom;
                outRect.bottom = topBottom;
            } else {
                outRect.top = (int) (((float) (spanCount - lp.getSpanIndex())) / spanCount * topBottom);
                outRect.bottom = (int) (((float) topBottom * (spanCount + 1) / spanCount) - outRect.top);
            }
        }
    }

    public int getSpanGroupIndex(int adapterPosition, int spanCount, int spanSize) {
        int span = 0;
        int group = 0;
        int positionSpanSize = spanSize;
        for (int i = 0; i < adapterPosition; i++) {
            int size = spanSize;
            span += size;
            if (span == spanCount) {
                span = 0;
                group++;
            } else if (span > spanCount) {
                // did not fit, moving to next_normal row / column
                span = size;
                group++;
            }
        }
        if (span + positionSpanSize > spanCount) {
            group++;
        }
        return group;
    }
}
