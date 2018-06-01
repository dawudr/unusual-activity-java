package com.financialjuice.unusualactivity.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class StockCompositeKey implements Serializable {
    private String symbol;
    private Date date;

    public StockCompositeKey() {
    }

    public StockCompositeKey(String symbol, Date date) {
        this.symbol = symbol;
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockCompositeKey that = (StockCompositeKey) o;
        return Objects.equals(symbol, that.symbol) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {

        return Objects.hash(symbol, date);
    }
}
