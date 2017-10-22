package ru.arturvasilov.recyclerview.demo.diff;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import java.util.List;

import ru.arturvasilov.recyclerview.demo.DemoItem;

/**
 * @author Artur Vasilov
 */
public class DiffUtilCallback extends DiffUtil.Callback {

    // ...

    @NonNull
    private final List<DemoItem> oldItems;

    @NonNull
    private final List<DemoItem> newItems;

    public DiffUtilCallback(@NonNull List<DemoItem> oldItems, @NonNull List<DemoItem> newItems) {
        this.oldItems = oldItems;
        this.newItems = newItems;
    }

    @Override
    public int getOldListSize() {
        return oldItems.size();
    }

    @Override
    public int getNewListSize() {
        return newItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItems.get(oldItemPosition).equals(newItems.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItems.get(oldItemPosition).equals(newItems.get(newItemPosition));
    }
}
