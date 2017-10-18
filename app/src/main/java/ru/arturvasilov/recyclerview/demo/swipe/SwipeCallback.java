package ru.arturvasilov.recyclerview.demo.swipe;

/**
 * @author Artur Vasilov
 */
public interface SwipeCallback {

    void onSwipeChanged(int translationX);

    void onSwipedOut();
}
