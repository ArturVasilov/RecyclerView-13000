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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DemoItem demoItem = (DemoItem) o;
        return label.equals(demoItem.label);

    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }
}
