/*
 * This file is a part of the Yandex Search for Android project.
 *
 * (C) Copyright 2017 Yandex, LLC. All rights reserved.
 *
 * Author: Artur Vasilov <avasilov@yandex-team.ru>
 */
package ru.arturvasilov.recyclerview.demo.swipe;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import ru.arturvasilov.recyclerview.demo.DemoItem;
import ru.arturvasilov.recyclerview.demo.R;

public class SwipeDismissHolder extends RecyclerView.ViewHolder {

    public SwipeDismissHolder(@NonNull View itemView, @NonNull OnDismissListener onDismissListener) {
        super(itemView);
        SwipeLayout swipeLayout = itemView.findViewById(R.id.swipe_view_layout);
        //swipeLayout.setSwipeEnabled(false);
        swipeLayout.setSwipeCallback(new SwipeCallback() {
            @Override
            public void onSwipeChanged(int translationX) {
            }

            @Override
            public void onSwipedOut() {
                DemoItem demoItem = (DemoItem) itemView.getTag(R.id.demo_item_key);
                onDismissListener.onItemDismissed(itemView, demoItem);
            }
        });
    }

    @CallSuper
    public void bind(@NonNull DemoItem demoItem) {
        itemView.setTag(R.id.demo_item_key, demoItem);
    }
}
