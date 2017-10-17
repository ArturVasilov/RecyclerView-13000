package ru.arturvasilov.recyclerview.demo.snap;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Artur Vasilov
 */
public class FixedSnapHelper extends LinearSnapHelper {

    @Nullable
    private OrientationHelper mVerticalHelper;

    @Nullable
    private OrientationHelper mHorizontalHelper;

    @Size(2)
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                              @NonNull View targetView) {
        int[] distanceToSnap = new int[2];

        if (layoutManager.canScrollHorizontally()) {
            distanceToSnap[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager));
        } else {
            distanceToSnap[0] = 0;
        }

        if (layoutManager.canScrollVertically()) {
            distanceToSnap[1] = distanceToStart(targetView, getVerticalHelper(layoutManager));
        } else {
            distanceToSnap[1] = 0;
        }
        return distanceToSnap;
    }

    @Override
    public View findSnapView(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            if (layoutManager.canScrollHorizontally()) {
                return getStartView(layoutManager, getHorizontalHelper(layoutManager));
            } else {
                return getStartView(layoutManager, getVerticalHelper(layoutManager));
            }
        }

        return super.findSnapView(layoutManager);
    }

    private int distanceToStart(@NonNull View targetView, @NonNull OrientationHelper helper) {
        return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
    }

    @Nullable
    private View getStartView(@NonNull RecyclerView.LayoutManager layoutManager,
                              @NonNull OrientationHelper helper) {
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) layoutManager);
            int firstChild = linearLayoutManager.findFirstVisibleItemPosition();
            boolean isLastItem = linearLayoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1;

            if (firstChild == RecyclerView.NO_POSITION || isLastItem) {
                return null;
            }

            View child = layoutManager.findViewByPosition(firstChild);
            if (helper.getDecoratedEnd(child) >= helper.getDecoratedMeasurement(child) / 2 && helper.getDecoratedEnd(child) > 0) {
                return child;
            } else {
                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1) {
                    return null;
                } else {
                    return layoutManager.findViewByPosition(firstChild + 1);
                }
            }
        }

        return super.findSnapView(layoutManager);
    }

    @NonNull
    private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (mVerticalHelper == null) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return mVerticalHelper;
    }

    @NonNull
    private OrientationHelper getHorizontalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (mHorizontalHelper == null) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return mHorizontalHelper;
    }
}
