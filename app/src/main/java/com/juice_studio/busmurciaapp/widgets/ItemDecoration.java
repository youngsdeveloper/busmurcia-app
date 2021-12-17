package com.juice_studio.busmurciaapp.widgets;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class ItemDecoration extends RecyclerView.ItemDecoration {

    /**
     *
     * {@link #startPadding} and {@link #endPadding} are final and required on initialization
     * because  {@link android.support.v7.widget.RecyclerView.ItemDecoration} are drawn
     * before the adapter's child views so you cannot rely on the child view measurements
     * to determine padding as the two are connascent
     *
     * see {@see <a href="https://en.wikipedia.org/wiki/Connascence_(computer_programming)"}
     */

    /**
     * @param startPadding
     * @param endPadding
     */
    private final int startPadding;
    private final int endPadding;

    public ItemDecoration(int startPadding, int endPadding) {
        this.startPadding = startPadding;
        this.endPadding = endPadding;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {

        int totalWidth = parent.getWidth();

        //first element
        if (parent.getChildAdapterPosition(view) == 0) {
            int firstPadding = (totalWidth - startPadding) / 2;
            firstPadding = Math.max(0, firstPadding);
            outRect.set(firstPadding, 0, 0, 0);
        }

        //last element
        if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1 &&
                parent.getAdapter().getItemCount() > 1) {
            int lastPadding = (totalWidth - endPadding) / 2;
            lastPadding = Math.max(0, lastPadding);
            outRect.set(0, 0, lastPadding, 0);
        }
    }

}
