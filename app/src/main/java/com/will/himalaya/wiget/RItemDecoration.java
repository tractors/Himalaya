package com.will.himalaya.wiget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

public class RItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = UIUtil.dip2px(view.getContext(),5);
        outRect.bottom = UIUtil.dip2px(view.getContext(),5);
        outRect.left = UIUtil.dip2px(view.getContext(),5);
        outRect.right = UIUtil.dip2px(view.getContext(),5);
    }
}
