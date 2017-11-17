package ru.arturvasilov.recyclerview.demo;

import android.support.annotation.NonNull;

/**
 * @author Artur Vasilov
 */
public class DemoItem {

    @NonNull
    private final String label;

    @NonNull
    private final String timeLabel;

    DemoItem(@NonNull String label) {
        this(label, null);
    }

    DemoItem(@NonNull String label, @NonNull String timeLabel) {
        this.label = label;
        this.timeLabel = timeLabel;
    }

    @NonNull
    String getLabel() {
        return label;
    }

    @NonNull
    public String getTimeLabel() {
        return timeLabel;
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
