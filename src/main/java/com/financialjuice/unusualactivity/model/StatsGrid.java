package com.financialjuice.unusualactivity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "StatsGrid")
@IdClass(StatsCompositeKey.class)
@Table(name="statsgrid",
        uniqueConstraints=@UniqueConstraint(columnNames={"symbol","time_part"}),
        indexes = {@Index(name = "i_statsgrid", columnList = "symbol,time_part")})
public class StatsGrid implements Serializable {

    @Id
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="date")
    private Date date;
    @Id
    @Column(name="symbol", length=10)
    private String symbol;
    private double normalDist_Volume;
    private double normalDist_PRange;
    private double pRange;
    private long latestVolume;
    @Column(name="name", length=100)
    private String name;
    @Column(name="news", length=20000)
    private String news;
    @Basic
    @Temporal(TemporalType.DATE)
    private Date date_part;
    @Basic
    @Column(name="time_part")
    private Time time_part;
    @Transient
    private List<Long> volumes;

    public StatsGrid() {
    }

    public StatsGrid(Date date, String symbol, double normalDist_Volume, double normalDist_PRange, long latestVolume, double pRange, String name, String news, Date date_part, Time time_part) {
        this.date = date;
        this.symbol = symbol;
        this.normalDist_Volume = normalDist_Volume;
        this.normalDist_PRange = normalDist_PRange;
        this.pRange = pRange;
        this.latestVolume = latestVolume;
        this.name = name;
        this.news = news;
        this.date_part = date_part;
        this.time_part = time_part;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getNormalDist_Volume() {
        return normalDist_Volume;
    }

    public void setNormalDist_Volume(double normalDist_Volume) {
        this.normalDist_Volume = normalDist_Volume;
    }

    public double getNormalDist_PRange() {
        return normalDist_PRange;
    }

    public void setNormalDist_PRange(double normalDist_PRange) {
        this.normalDist_PRange = normalDist_PRange;
    }

    public double getpRange() {
        return pRange;
    }

    public void setpRange(double pRange) {
        this.pRange = pRange;
    }

    public long getLatestVolume() {
        return latestVolume;
    }

    public void setLatestVolume(long latestVolume) {
        this.latestVolume = latestVolume;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public Date getDate_part() {
        return date_part;
    }

    public void setDate_part(Date date_part) {
        this.date_part = date_part;
    }

    public Time getTime_part() {
        return time_part;
    }

    public void setTime_part(Time time_part) {
        this.time_part = time_part;
    }

    public List<Long> getVolumes() {
        return volumes;
    }

    public void setVolumes(List<Long> volumes) {
        this.volumes = volumes;
    }

    @Override
    public String toString() {
        return "StatsGrid{" +
                "date=" + date +
                ", symbol='" + symbol + '\'' +
                ", normalDist_Volume=" + normalDist_Volume +
                ", normalDist_PRange=" + normalDist_PRange +
                ", pRange=" + pRange +
                ", latestVolume=" + latestVolume +
                ", name='" + name + '\'' +
                ", news='" + news + '\'' +
                ", date_part=" + date_part +
                ", time_part=" + time_part +
                ", volumes=" + volumes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatsGrid statsGrid = (StatsGrid) o;
        return Double.compare(statsGrid.normalDist_Volume, normalDist_Volume) == 0 &&
                Double.compare(statsGrid.normalDist_PRange, normalDist_PRange) == 0 &&
                Double.compare(statsGrid.pRange, pRange) == 0 &&
                latestVolume == statsGrid.latestVolume &&
                Objects.equals(date, statsGrid.date) &&
                Objects.equals(symbol, statsGrid.symbol) &&
                Objects.equals(name, statsGrid.name) &&
                Objects.equals(news, statsGrid.news) &&
                Objects.equals(date_part, statsGrid.date_part) &&
                Objects.equals(time_part, statsGrid.time_part);
    }

    @Override
    public int hashCode() {

        return Objects.hash(date, symbol, normalDist_Volume, normalDist_PRange, pRange, latestVolume, name, news, date_part, time_part);
    }
}


