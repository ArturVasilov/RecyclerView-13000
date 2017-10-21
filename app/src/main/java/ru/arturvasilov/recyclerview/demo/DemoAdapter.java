package ru.arturvasilov.recyclerview.demo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.arturvasilov.recyclerview.demo.swipe.OnDismissListener;
import ru.arturvasilov.recyclerview.demo.swipe.SwipeDismissHolder;

/**
 * @author Artur Vasilov
 */
class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.DemoHolder> {

    @NonNull
    private final List<DemoItem> items;

    @NonNull
    private final OnDismissListener onDismissListener;

    @NonNull
    private final OnItemClickListener onItemClickListener;

    @NonNull
    private final View.OnClickListener internalClickListener = new View.OnClickListener() {
        @Override
        public void onClick(@NonNull View view) {
            DemoItem demoItem = (DemoItem) view.getTag(R.id.demo_item_key);
            if (demoItem != null) {
                int position = items.indexOf(demoItem);
                onItemClickListener.onItemClick(demoItem, position);
            }
        }
    };

    DemoAdapter(@NonNull List<DemoItem> items, @NonNull OnDismissListener onDismissListener,
                @NonNull OnItemClickListener onItemClickListener) {
        this.items = new ArrayList<>(items);
        this.onDismissListener = onDismissListener;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public DemoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recycler_view_item, parent, false);
        return new DemoHolder(itemView, onDismissListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DemoHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(@NonNull DemoItem demoItem, int position) {
        if (position < 0 || position > items.size()) {
            throw new IllegalArgumentException("Position must be in list bounds");
        }
        items.add(position, demoItem);
        notifyItemInserted(position);
    }

    public void removeItem(@NonNull DemoItem demoItem) {
        int position = items.indexOf(demoItem);
        items.remove(position);
        notifyItemRemoved(position);
    }

    interface OnItemClickListener {
        void onItemClick(@NonNull DemoItem demoItem, int position);
    }

    class DemoHolder extends SwipeDismissHolder {

        @NonNull
        private final TextView labelTextView;

        DemoHolder(@NonNull View itemView, @NonNull OnDismissListener onDismissListener) {
            super(itemView, onDismissListener);
            labelTextView = itemView.findViewById(R.id.item_label);
        }

        @Override
        public void bind(@NonNull DemoItem demoItem) {
            super.bind(demoItem);
            itemView.setOnClickListener(internalClickListener);
            labelTextView.setText(demoItem.getLabel());
        }
    }
}
