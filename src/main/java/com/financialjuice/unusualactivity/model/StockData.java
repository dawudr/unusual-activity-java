package com.financialjuice.unusualactivity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class StockData extends AuditData implements Comparable<StockData> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private Date date;
    @NotBlank
    private String symbol;
    private double open;
    private double close;
    private double high;
    private double low;
    private long volume;

//    @ManyToOne(optional=true)
//    @JoinColumn(name = "symbol", insertable=false, updatable=false)
//    private SymbolData symbolData;

    public StockData() {
        if (this.getCreatedAt() == null)
            this.setCreatedAt(new Date());
        this.setUpdatedAt(new Date());
    }

    public StockData(Date date, String symbol, Double open, Double close, Double high, Double low, Long volume) {
        this.date = date;
        this.symbol = symbol;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
        if (this.getCreatedAt() == null)
            this.setCreatedAt(new Date());
        this.setUpdatedAt(new Date());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

//    public SymbolData getSymbolData() {
//        return symbolData;
//    }
//
//    public void setSymbolData(SymbolData symbolData) {
//        this.symbolData = symbolData;
//    }

    @Override
    public String toString() {
        return "StockData{" +
                "id=" + id +
                ", date=" + date +
                ", symbol='" + symbol + '\'' +
                ", open=" + open +
                ", close=" + close +
                ", high=" + high +
                ", low=" + low +
                ", volume=" + volume +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockData stockData = (StockData) o;
        return Double.compare(stockData.open, open) == 0 &&
                Double.compare(stockData.close, close) == 0 &&
                Double.compare(stockData.high, high) == 0 &&
                Double.compare(stockData.low, low) == 0 &&
                volume == stockData.volume &&
                Objects.equals(date, stockData.date) &&
                Objects.equals(symbol, stockData.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, symbol, open, close, high, low, volume);
    }

    @Override
    public int compareTo(StockData o) {
        return symbol.compareTo(o.getSymbol());
    }
}
