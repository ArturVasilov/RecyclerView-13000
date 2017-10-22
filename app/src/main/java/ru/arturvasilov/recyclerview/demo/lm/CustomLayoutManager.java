package ru.arturvasilov.recyclerview.demo.lm;

import android.support.v7.widget.RecyclerView;

/**
 * @author Artur Vasilov
 */
public class CustomLayoutManager extends RecyclerView.LayoutManager {

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return null;
    }

    @Override
    public void collectInitialPrefetchPositions(int adapterItemCount,
                                                LayoutPrefetchRegistry layoutPrefetchRegistry) {
        super.collectInitialPrefetchPositions(adapterItemCount, layoutPrefetchRegistry);
    }

    @Override
    public void collectAdjacentPrefetchPositions(int dx, int dy, RecyclerView.State state,
                                                 LayoutPrefetchRegistry layoutPrefetchRegistry) {
        layoutPrefetchRegistry.addPosition(0, 0);
        layoutPrefetchRegistry.addPosition(1, 500);
        // ...
    }
}
