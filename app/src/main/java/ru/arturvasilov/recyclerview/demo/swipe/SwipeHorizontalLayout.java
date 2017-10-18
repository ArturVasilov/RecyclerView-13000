package ru.arturvasilov.recyclerview.demo.swipe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewParent;

import ru.arturvasilov.recyclerview.demo.R;

/**
 * @author Artur Vasilov
 */
public class SwipeHorizontalLayout extends SwipeLayout {

    @Nullable
    private ValueAnimator translationAnimator;

    public SwipeHorizontalLayout(Context context) {
        super(context);
    }

    public SwipeHorizontalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeHorizontalLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent event) {
        if (!isSwipeEnabled) {
            return super.onInterceptTouchEvent(event);
        }
        if (Math.abs(contentView.getTranslationX()) >= contentView.getWidth()) {
            //already closed view, ignore new events
            return super.onInterceptTouchEvent(event);
        }
        if (translationAnimator != null && translationAnimator.isRunning()) {
            return super.onInterceptTouchEvent(event);
        }

        boolean isIntercepted = super.onInterceptTouchEvent(event);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = lastX = (int) event.getX();
                downY = (int) event.getY();
                isIntercepted = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int disX = (int) (event.getX() - downX);
                int disY = (int) (event.getY() - downY);
                isIntercepted = Math.abs(disX) > scaledTouchSlop && Math.abs(disX) > Math.abs(disY);
                break;
            case MotionEvent.ACTION_UP:
                isIntercepted = false;
                // menu view opened and click on content view,
                // we just close the menu view and intercept the up event
                if (isMenuOpen() && isClickOnContentView(event.getX())) {
                    smoothCloseMenu();
                    isIntercepted = true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                isIntercepted = false;
                if (translationAnimator != null && translationAnimator.isRunning()) {
                    translationAnimator.end();
                }
                break;
        }
        return isIntercepted;
    }

    boolean isMenuOpen() {
        return isSwipeMenuEnabled && Math.abs(contentView.getTranslationX()) >= menuView.getWidth();
    }

    private boolean isClickOnContentView(float clickX) {
        return !isSwipeMenuEnabled
                || contentView.getTranslationX() > 0
                || clickX < (contentView.getWidth() - menuView.getWidth());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isSwipeEnabled) {
            return super.onTouchEvent(event);
        }
        if (translationAnimator != null && translationAnimator.isRunning()) {
            return super.onTouchEvent(event);
        }
        if (Math.abs(contentView.getTranslationX()) >= contentView.getWidth()) {
            //already closed view, ignore new events
            return super.onTouchEvent(event);
        }

        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);

        int dx;
        int dy;
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int disX = (int) (lastX - event.getX());
                int disY = (int) (lastY - event.getY());
                if (!dragging
                        && Math.abs(disX) > scaledTouchSlop
                        && Math.abs(disX) > Math.abs(disY)) {
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    dragging = true;
                }
                if (dragging) {
                    float translationX = contentView.getTranslationX() - disX;
                    if (translationX > 0 && isSwipeToDeleteEnabled && translationX < contentView.getWidth()) {
                        //user swipes from left to right, we show delete icon
                        hideMenuView();
                        contentView.setTranslationX(translationX);
                        if (swipeCallback != null) {
                            swipeCallback.onSwipeChanged((int) translationX);
                        }
                    }
                    if (translationX < 0 && isSwipeMenuEnabled && Math.abs(translationX) <= menuView.getWidth()) {
                        //user swipes from right to left, we show context menu
                        showMenuView();
                        contentView.setTranslationX(translationX);
                        if (swipeCallback != null) {
                            swipeCallback.onSwipeChanged((int) translationX);
                        }
                    }
                    lastX = (int) event.getX();
                    lastY = (int) event.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
                ViewParent parent = getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
                dx = (int) (downX - event.getX());
                dy = (int) (downY - event.getY());
                dragging = false;
                velocityTracker.computeCurrentVelocity(1000, scaledMaximumFlingVelocity);
                int velocityX = (int) velocityTracker.getXVelocity();
                int velocity = Math.abs(velocityX);
                if (velocity > scaledMinimumFlingVelocity) {
                    //swipe to dismiss (only if swipe is long enough)
                    if (isSwipeToDeleteEnabled && contentView.getTranslationX() > contentView.getWidth() / 3) {
                        //swipe to delete action
                        if (velocityX > 0) {
                            smoothSwipeOutItem();
                        } else {
                            smoothSwipeBackItem();
                        }
                    } else if (isSwipeMenuEnabled && contentView.getTranslationX() < 0) {
                        //swipe to to show context menu action
                        int duration = getSwipeToDismissDuration(event, velocity);
                        if (velocityX < 0) {
                            smoothOpenMenu(duration);
                        } else {
                            smoothCloseMenu(duration);
                        }
                    } else {
                        //not clearly determined case, just judge what to do with swiped content
                        judgeOpenClose(dx, dy);
                    }
                } else {
                    //swipe is slow, just judge what to do with swiped content
                    judgeOpenClose(dx, dy);
                }
                velocityTracker.clear();
                velocityTracker.recycle();
                velocityTracker = null;
                if (Math.abs(dx) > scaledTouchSlop
                        || Math.abs(dy) > scaledTouchSlop
                        || isMenuOpen()) { // ignore click listener, cancel this event
                    MotionEvent motionEvent = MotionEvent.obtain(event);
                    motionEvent.setAction(MotionEvent.ACTION_CANCEL);
                    return super.onTouchEvent(motionEvent);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                dragging = false;
                if (translationAnimator != null && translationAnimator.isRunning()) {
                    translationAnimator.end();
                } else {
                    dx = (int) (downX - event.getX());
                    dy = (int) (downY - event.getY());
                    judgeOpenClose(dx, dy);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void hideMenuView() {
        if (menuView != null) {
            menuView.setVisibility(GONE);
        }
    }

    private void showMenuView() {
        if (menuView != null) {
            menuView.setVisibility(VISIBLE);
        }
    }

    private void smoothSwipeOutItem() {
        hideMenuView();

        if (!isSwipeToDeleteEnabled) {
            return;
        }
        int duration = (int) (translationDuration * (1 - contentView.getTranslationX() / contentView.getWidth()));
        if (duration < translationDuration / 3) {
            duration = translationDuration / 3;
        }
        translateContentView(duration, contentView.getTranslationX(), contentView.getWidth(),
                () -> {
                    if (swipeCallback != null) {
                        swipeCallback.onSwipedOut();
                    }
                });
    }

    private void smoothSwipeBackItem() {
        if (!isSwipeToDeleteEnabled) {
            return;
        }
        int duration = (int) (translationDuration * contentView.getTranslationX() / contentView.getWidth());
        if (duration < translationDuration / 3) {
            duration = translationDuration / 3;
        }
        translateContentView(duration, contentView.getTranslationX(), 0, null);
    }

    @Override
    public void smoothOpenMenu(int duration) {
        showMenuView();

        if (isSwipeMenuEnabled) {
            translateContentView(duration, contentView.getTranslationX(), -menuView.getWidth(), null);
        }
    }

    @Override
    public void smoothCloseMenu(int duration) {
        showMenuView();

        if (isSwipeMenuEnabled) {
            translateContentView(duration, contentView.getTranslationX(), 0, null);
        }
    }

    private void judgeOpenClose(int dx, int dy) {
        if (isSwipeToDeleteEnabled && contentView.getTranslationX() > 0) {
            if (contentView.getTranslationX() > contentView.getWidth() * swipeToDeletePercent) {
                smoothSwipeOutItem();
            } else {
                smoothSwipeBackItem();
            }
        } else if (isSwipeMenuEnabled && Math.abs(contentView.getTranslationX()) >= (menuView.getWidth() * swipeMenuOpenPercent)) {
            if (Math.abs(dx) > scaledTouchSlop || Math.abs(dy) > scaledTouchSlop) {
                if (isMenuOpen()) {
                    smoothCloseMenu();
                } else {
                    smoothOpenMenu();
                }
            }
        } else {
            showMenuView();
            translateContentView(translationDuration, contentView.getTranslationX(), 0, null);
        }
    }

    private void translateContentView(int duration, float from, float to, @Nullable Runnable endAction) {
        if (translationAnimator != null && translationAnimator.isRunning()) {
            translationAnimator.end();
        }

        translationAnimator = ValueAnimator.ofFloat(from, to);
        //noinspection ConstantConditions
        translationAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                translationAnimator = null;
                if (endAction != null) {
                    endAction.run();
                }
            }
        });
        translationAnimator.addUpdateListener(animation -> {
            float translationX = (float) animation.getAnimatedValue();
            contentView.setTranslationX(translationX);
            if (swipeCallback != null) {
                swipeCallback.onSwipeChanged((int) translationX);
            }
        });
        translationAnimator.setDuration(duration).start();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setClickable(true);
        contentView = findViewById(R.id.swipe_view_content);
        if (contentView == null) {
            throw new IllegalArgumentException("Content view not found");
        }

        menuView = findViewById(R.id.swipe_view_menu);
        if (menuView == null && isSwipeMenuEnabled) {
            throw new IllegalArgumentException("Swipe menu enabled, but swipe menu view is null");
        }
    }

    @Override
    int getMenuWidth() {
        if (isSwipeMenuEnabled) {
            return menuView.getWidth();
        }
        return 0;
    }

    @Override
    int getMoveLen(@NonNull MotionEvent event) {
        float translationX = contentView.getTranslationX();
        return (int) (event.getX() - translationX);
    }
}
