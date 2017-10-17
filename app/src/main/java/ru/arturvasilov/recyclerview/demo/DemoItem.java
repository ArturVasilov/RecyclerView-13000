package ru.arturvasilov.recyclerview.demo;

import android.support.annotation.NonNull;

/**
 * @author Artur Vasilov
 */
public class DemoItem {

    @NonNull
    private final String label;

    DemoItem(@NonNull String label) {
        this.label = label;
    }

    @NonNull
    String getLabel() {
        return label;
    }
}
