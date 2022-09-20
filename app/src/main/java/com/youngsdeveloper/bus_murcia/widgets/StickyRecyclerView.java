package com.youngsdeveloper.bus_murcia.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StickyRecyclerView extends RecyclerView {

    /**
     * The horizontal distance (in pixels) scrolled is > 0
     *
     * @see #getScrollDirection()
     */
    public static final int SCROLL_DIRECTION_LEFT = 0;

    /**
     * The horizontal distance scrolled (in pixels) is < 0
     *
     * @see #getScrollDirection()
     */
    public static final int SCROLL_DIRECTION_RIGHT = 1;

    private int mScrollDirection;
    private OnCenterItemChangedListener mCenterItemChangedListener;

    public StickyRecyclerView(Context context) {
        super(context);
    }

    public StickyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StickyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == SCROLL_STATE_IDLE) {
            if (mCenterItemChangedListener != null) {
                mCenterItemChangedListener.onCenterItemChanged(findCenterViewIndex());
            }
        }
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        setScrollDirection(dx);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float percentage = getPercentageFromCenter(child);
            float scale = 1f - (0.4f * percentage);

            child.setScaleX(scale);
            child.setScaleY(scale);
        }
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        smoothScrollToCenter();
        return true;
    }

    private float getPercentageFromCenter(View child) {
        float centerX = (getMeasuredWidth() / 2);
        float childCenterX = child.getX() + (child.getWidth() / 2);
        float offSet = Math.max(centerX, childCenterX) - Math.min(centerX, childCenterX);
        int maxOffset = (getMeasuredWidth() / 2) + child.getWidth();
        return (offSet / maxOffset);
    }

    private int findCenterViewIndex() {
        int count = getChildCount();
        int index = -1;
        int closest = Integer.MAX_VALUE;
        int centerX = (getMeasuredWidth() / 2);

        for (int i = 0; i < count; ++i) {
            View child = getLayoutManager().getChildAt(i);
            int childCenterX = (int) (child.getX() + (child.getWidth() / 2));
            int distance = Math.abs(centerX - childCenterX);
            if (distance < closest) {
                closest = distance;
                index = i;
            }
        }

        if (index == -1) {
            throw new IllegalStateException("Can\'t find central view.");
        } else {
            return index;
        }
    }

    private void smoothScrollToCenter() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) getLayoutManager();
        int lastVisibleView = linearLayoutManager.findLastVisibleItemPosition();
        int firstVisibleView = linearLayoutManager.findFirstVisibleItemPosition();
        View firstView = linearLayoutManager.findViewByPosition(firstVisibleView);
        View lastView = linearLayoutManager.findViewByPosition(lastVisibleView);
        int screenWidth = this.getWidth();

        //since views have variable sizes, we need to calculate side margins separately.
        int leftMargin = (screenWidth - lastView.getWidth()) / 2;
        int rightMargin = (screenWidth - firstView.getWidth()) / 2 + firstView.getWidth();
        int leftEdge = lastView.getLeft();
        int rightEdge = firstView.getRight();
        int scrollDistanceLeft = leftEdge - leftMargin;
        int scrollDistanceRight = rightMargin - rightEdge;

        if (mScrollDirection == SCROLL_DIRECTION_LEFT) {
            smoothScrollBy(scrollDistanceLeft, 0);
        } else if (mScrollDirection == SCROLL_DIRECTION_RIGHT) {
            smoothScrollBy(-scrollDistanceRight, 0);
        }
    }

    /**
     * Returns the last recorded scrolling direction of the StickyRecyclerView as
     * set in {@link  #onScrolled}
     *
     * @return {@link #SCROLL_DIRECTION_LEFT} or {@link #SCROLL_DIRECTION_RIGHT}
     */
    public int getScrollDirection() {
        return mScrollDirection;
    }

    private void setScrollDirection(int dx) {
        mScrollDirection = dx >= 0 ? SCROLL_DIRECTION_LEFT : SCROLL_DIRECTION_RIGHT;
    }

    /**
     * Set a listener that will be notified when the central item is changed.
     *
     * @param listener Listener to set or null to clear
     */
    public void setOnCenterItemChangedListener(OnCenterItemChangedListener listener) {
        mCenterItemChangedListener = listener;
    }

    /**
     * A listener interface that can be added to the {@link StickyRecyclerView} to get
     * notified when the central item is changed.
     */
    public interface OnCenterItemChangedListener {

        /**
         * @param centerPosition position of the center item
         */
        void onCenterItemChanged(int centerPosition);
    }
}