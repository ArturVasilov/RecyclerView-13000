package ru.arturvasilov.recyclerview.demo.swipemenu;

import android.support.annotation.NonNull;
import android.view.View;

import ru.arturvasilov.recyclerview.demo.DemoItem;

/**
 * Interface for Swipe-to-Dismiss pattern in RecyclerView
 *
 * @author Artur Vasilov
 */
public interface OnDismissListener {

    /**
     * Indicates that some holder was dismissed
     *
     * @param itemView - itemView of the dismissed holder
     * @param demoItem - item associated with dismissed holder
     */
    void onItemDismissed(@NonNull View itemView, @NonNull DemoItem demoItem);
}