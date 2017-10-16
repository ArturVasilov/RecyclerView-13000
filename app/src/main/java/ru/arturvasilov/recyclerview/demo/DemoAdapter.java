package ru.arturvasilov.recyclerview.demo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * @author Artur Vasilov
 */
class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.DemoHolder> {

    @NonNull
    private final List<DemoItem> items;

    DemoAdapter(@NonNull List<DemoItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public DemoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new DemoHolder(inflater.inflate(R.layout.recycler_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DemoHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class DemoHolder extends RecyclerView.ViewHolder {

        @NonNull
        private final TextView labelTextView;

        DemoHolder(@NonNull View itemView) {
            super(itemView);
            labelTextView = itemView.findViewById(R.id.item_label);
        }

        void bind(@NonNull DemoItem demoItem) {
            labelTextView.setText(demoItem.getLabel());
        }
    }
}
