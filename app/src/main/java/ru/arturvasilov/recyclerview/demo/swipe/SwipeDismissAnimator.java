package ru.arturvasilov.recyclerview.demo.swipe;

import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import ru.arturvasilov.recyclerview.demo.R;

/**
 * @author ArturVasilov
 */
public class SwipeDismissAnimator extends DefaultItemAnimator {

    @Override
    public void onRemoveFinished(@NonNull RecyclerView.ViewHolder item) {
        View contentView = item.itemView.findViewById(R.id.swipe_view_content);
        if (contentView != null) {
            contentView.setTranslationX(0);
        }
    }
}
