package ru.arturvasilov.recyclerview.demo.animation;

import android.support.v7.widget.DefaultItemAnimator;

/**
 * @author ArturVasilov
 */
public class LongRunningItemAnimator extends DefaultItemAnimator {

    private static final long LONG_ANIMATION_DURATION = 3000;

    @Override
    public long getAddDuration() {
        return LONG_ANIMATION_DURATION;
    }

    @Override
    public long getRemoveDuration() {
        return LONG_ANIMATION_DURATION;
    }

    // ...
}
