package com.hdpfans.app.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CenterLinearLayoutManager extends LinearLayoutManager {
    public CenterLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
        int[] scrollAmount = getChildRectangleOnScreenScrollAmount(parent, child, rect,
                immediate);
        int dx = scrollAmount[0];
        int dy = scrollAmount[1];
        if (!focusedChildVisible) {
            if (dx != 0 || dy != 0) {
//                if (immediate) {
                    parent.scrollBy(dx, dy);
//                } else {
//                    parent.smoothScrollBy(dx, dy);
//                }
                return true;
            }
        }
        return false;
    }

    private int[] getChildRectangleOnScreenScrollAmount(RecyclerView parent, View child,
                                                        Rect rect, boolean immediate) {
        int[] out = new int[2];
        final int parentLeft = getPaddingLeft();
        final int parentRight = getWidth() - getPaddingRight();
        final int parentBottom = (getHeight() - getPaddingBottom()) / 2;
        final int parentTop = (parentBottom - getPaddingTop()) / 2;
        final int childLeft = child.getLeft() + rect.left - child.getScrollX();
        final int childTop = child.getTop() + rect.top - child.getScrollY();
        final int childRight = childLeft + rect.width();
        final int childBottom = childTop + rect.height();

        final int offScreenLeft = Math.min(0, childLeft - parentLeft);
        final int offScreenTop = Math.min(0, childTop - parentTop);
        final int offScreenRight = Math.max(0, childRight - parentRight);
        final int offScreenBottom = Math.max(0, childBottom - parentBottom);

        // Favor the "start" layout direction over the end when bringing one side or the other
        // of a large rect into view. If we decide to bring in end because start is already
        // visible, limit the scroll such that start won't go out of bounds.
        final int dx;
        if (getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL) {
            dx = offScreenRight != 0 ? offScreenRight
                    : Math.max(offScreenLeft, childRight - parentRight);
        } else {
            dx = offScreenLeft != 0 ? offScreenLeft
                    : Math.min(childLeft - parentLeft, offScreenRight);
        }

        // Favor bringing the top into view over the bottom. If top is already visible and
        // we should scroll to make bottom visible, make sure top does not go out of bounds.
        final int dy = offScreenTop != 0 ? offScreenTop
                : Math.min(childTop - parentTop - rect.bottom, offScreenBottom);
        out[0] = dx;
        out[1] = dy;
        return out;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        RecyclerView.SmoothScroller smoothScroller = new CenterSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    private static class CenterSmoothScroller extends LinearSmoothScroller {

        CenterSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
        }
    }
}
