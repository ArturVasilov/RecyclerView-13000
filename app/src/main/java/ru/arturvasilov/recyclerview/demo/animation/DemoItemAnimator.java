package ru.arturvasilov.recyclerview.demo.animation;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import ru.arturvasilov.recyclerview.demo.R;

/**
 * @author ArturVasilov
 */
public class DemoItemAnimator extends BaseItemAnimator {

    @Override
    protected void preAnimateRemoveImpl(RecyclerView.ViewHolder holder) {
        View swipeView = holder.itemView.findViewById(R.id.swipe_view_content);
        if (swipeView != null) {
            swipeView.setTranslationX(0);
        }
        holder.itemView.setScaleX(1);
        holder.itemView.setScaleY(1);
    }

    @Override
    protected void animateRemoveImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .scaleX(0.5f)
                .scaleY(0.5f)
                .setDuration(getRemoveDuration())
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new DefaultRemoveVpaListener(holder))
                .setStartDelay(getRemoveDelay(holder))
                .start();
    }

    @Override
    protected void preAnimateAddImpl(RecyclerView.ViewHolder holder) {
        holder.itemView.setScaleX(0.5f);
        holder.itemView.setScaleY(0.5f);
    }

    @Override
    protected void animateAddImpl(RecyclerView.ViewHolder holder) {
        ViewCompat.animate(holder.itemView)
                .scaleX(1)
                .scaleY(1)
                .setDuration(getAddDuration())
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new DefaultAddVpaListener(holder))
                .setStartDelay(getAddDelay(holder))
                .start();
    }
}
