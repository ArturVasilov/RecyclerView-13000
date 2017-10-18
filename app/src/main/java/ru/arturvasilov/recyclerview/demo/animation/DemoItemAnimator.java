package ru.arturvasilov.recyclerview.demo.animation;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.LinearInterpolator;

import ru.arturvasilov.recyclerview.demo.R;

/**
 * @author ArturVasilov
 */
public class DemoItemAnimator extends BaseItemAnimator {

    @Override
    protected void animateRemoveHolder(@NonNull RecyclerView.ViewHolder holder) {
        //holder.itemView.setTranslationX(0);
        View view = holder.itemView.findViewById(R.id.swipe_view_content);
        view.setTranslationX(0);
        ViewCompat.animate(holder.itemView)
                .rotation(270)
                .setDuration(getRemoveDuration())
                .setInterpolator(new LinearInterpolator())
                .setListener(new DefaultAddVpaListener(holder))
                .setStartDelay(getRemoveDelay(holder))
                .start();
    }



    @Override
    public long getRemoveDuration() {
        return 3000;
    }
}
