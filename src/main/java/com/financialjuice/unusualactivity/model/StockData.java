package com.financialjuice.unusualactivity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "StockData")
@IdClass(StockCompositeKey.class)
@Table(name="stockdata",
        uniqueConstraints=@UniqueConstraint(columnNames={"symbol","date"}),
        indexes = {@Index(name = "i_stockdata", columnList = "symbol,date")})
public class StockData extends AuditData implements Comparable<StockData> {

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private long id;
    @Id
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="date")
    private Date date;
    @Id
    @Column(name="symbol", length=10)
    private String symbol;
    private double open;
    private double close;
    private double high;
    private double low;
    private long volume;
    @Basic
    @Temporal(TemporalType.DATE)
    private Date date_part;
    @Basic
    @Column(name="Time_part")
    private Time time_part;
    @Transient
    private String news;

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

    public StockData(Date date, String symbol, double open, double close, double high, double low, long volume, Date date_part, Time time_part, String news) {
        this.date = date;
        this.symbol = symbol;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
        this.date_part = date_part;
        this.time_part = time_part;
        this.news = news;
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

    public Date getDate_part() {
        return date_part;
    }

    public void setDate_part(java.sql.Date date_part) {
        this.date_part = date_part;
    }

    public Time getTime_part() {
        return time_part;
    }

    public void setTime_part(Time time_part) {
        this.time_part = time_part;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    @Override
    public String toString() {
        return "StockData{" +
                "date=" + date +
                ", symbol='" + symbol + '\'' +
                ", open=" + open +
                ", close=" + close +
                ", high=" + high +
                ", low=" + low +
                ", volume=" + volume +
                ", date_part=" + date_part +
                ", time_part=" + time_part +
                ", news='" + news + '\'' +
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

