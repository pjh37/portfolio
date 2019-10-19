package com.example.myfriends.fileList;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class FileFindItemDecoration extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int index=parent.getChildAdapterPosition(view)+1;
        outRect.set(30,30,30,30);
        view.setBackgroundColor(0xFFECE9E9);
        view.setElevation(20.0f);
    }
}
