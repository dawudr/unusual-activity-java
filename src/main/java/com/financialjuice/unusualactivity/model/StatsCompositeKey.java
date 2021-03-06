package com.financialjuice.unusualactivity.model;

import java.io.Serializable;
import java.sql.Time;
import java.util.Objects;

public class StatsCompositeKey implements Serializable {
    private String symbol;
    private Time time_part;

    public StatsCompositeKey() {
    }

    public StatsCompositeKey(String symbol, Time time_part) {
        this.symbol = symbol;
        this.time_part = time_part;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatsCompositeKey that = (StatsCompositeKey) o;
        return Objects.equals(symbol, that.symbol) &&
                Objects.equals(time_part, that.time_part);
    }

    @Override
    public int hashCode() {

        return Objects.hash(symbol, time_part);
    }
}