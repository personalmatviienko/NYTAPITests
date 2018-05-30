package data;

import java.util.Random;

public enum PeriodType {
    DAY("1"),
    WEEK("7"),
    MONTH("30"),
    INVALID("15");

    private final String text;

    PeriodType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
