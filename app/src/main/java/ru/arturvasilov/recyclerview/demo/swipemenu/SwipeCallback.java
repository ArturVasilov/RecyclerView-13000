package ru.arturvasilov.recyclerview.demo.swipemenu;

/**
 * @author Artur Vasilov
 */
public interface SwipeCallback {

    void onSwipeChanged(int translationX);

    void onSwipedOut();
}
