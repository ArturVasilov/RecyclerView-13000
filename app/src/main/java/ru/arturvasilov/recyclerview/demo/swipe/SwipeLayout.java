package ru.arturvasilov.recyclerview.demo.swipe;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import ru.arturvasilov.recyclerview.demo.R;

/**
 * @author Artur Vasilov
 *         <p>
 *         This is seriously modified version of SwipeMenu library from
 *         https://github.com/TUBB/SwipeMenu
 */
public abstract class SwipeLayout extends FrameLayout {

    private static final int DEFAULT_ANIMATION_DURATION = 250;
    private static final float DEFAULT_SWIPE_TO_DISMISS_PERCENT = 0.5f;
    private static final float DEFAULT_SWIPE_MENU_OPEN_PERCENT = 0.5f;

    protected View contentView;
    protected View menuView;

    protected int translationDuration = DEFAULT_ANIMATION_DURATION;
    protected int scaledTouchSlop;
    protected int lastX;
    protected int lastY;
    protected int downX;
    protected int downY;

    protected boolean isSwipeToDeleteEnabled = false;
    protected float swipeToDeletePercent = DEFAULT_SWIPE_TO_DISMISS_PERCENT;
    protected boolean isSwipeMenuEnabled = false;
    protected float swipeMenuOpenPercent = DEFAULT_SWIPE_MENU_OPEN_PERCENT;
    protected boolean dragging;
    protected VelocityTracker velocityTracker;
    protected int scaledMinimumFlingVelocity;
    protected int scaledMaximumFlingVelocity;

    protected boolean isSwipeEnabled = true;

    @Nullable
    protected SwipeCallback swipeCallback;

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (!isInEditMode()) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SwipeMenu, 0, defStyle);
            translationDuration = array.getInteger(R.styleable.SwipeMenu_swipe_menu_duration, DEFAULT_ANIMATION_DURATION);
            isSwipeToDeleteEnabled = array.getBoolean(R.styleable.SwipeMenu_swipe_to_dismiss_enabled, true);
            swipeToDeletePercent = array.getFloat(R.styleable.SwipeMenu_swipe_to_dismiss_percent, DEFAULT_SWIPE_TO_DISMISS_PERCENT);
            isSwipeMenuEnabled = array.getBoolean(R.styleable.SwipeMenu_swipe_menu_enabled, false);
            swipeMenuOpenPercent = array.getFloat(R.styleable.SwipeMenu_swipe_menu_open_percent, DEFAULT_SWIPE_MENU_OPEN_PERCENT);
            array.recycle();
        }
        init();
    }

    public void init() {
        ViewConfiguration viewConfig = ViewConfiguration.get(getContext());
        scaledTouchSlop = viewConfig.getScaledTouchSlop();
        scaledMinimumFlingVelocity = viewConfig.getScaledMinimumFlingVelocity();
        scaledMaximumFlingVelocity = viewConfig.getScaledMaximumFlingVelocity();
    }

    public void smoothOpenMenu() {
        smoothOpenMenu(translationDuration);
    }

    public abstract void smoothOpenMenu(int duration);

    public void smoothCloseMenu() {
        smoothCloseMenu(translationDuration);
    }

    public abstract void smoothCloseMenu(int duration);

    /**
     * compute finish duration
     *
     * @param ev       up event
     * @param velocity velocity
     * @return finish duration
     */
    int getSwipeToDismissDuration(MotionEvent ev, int velocity) {
        int moveLen = getMoveLen(ev);
        final int len = getMenuWidth();
        final int halfLen = len / 2;
        final float distanceRatio = Math.min(1f, 1.0f * Math.abs(moveLen) / len);
        final float distance = halfLen + halfLen * distanceInfluenceForSnapDuration(distanceRatio);
        int duration;
        if (velocity > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
        } else {
            final float pageDelta = (float) Math.abs(moveLen) / len;
            duration = (int) ((pageDelta + 1) * 100);
        }
        duration = Math.min(duration, translationDuration);
        return duration;
    }

    abstract int getMoveLen(MotionEvent event);

    abstract int getMenuWidth();

    float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }

    public void setSwipeCallback(@Nullable SwipeCallback swipeCallback) {
        this.swipeCallback = swipeCallback;
    }

    public void setSwipeEnabled(boolean swipeMenuEnabled) {
        isSwipeEnabled = swipeMenuEnabled;
    }
}
