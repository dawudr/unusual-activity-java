package com.financialjuice.unusualactivity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.Time;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StatsDTO {

    private Date dateCreated;
    private String symbol;
    private double normalDist_Volume;
    private Time time_part;
    private double normalDist_PRange;
    private double pRange;
    private long latestVolume;
    private String name;

    public StatsDTO() {
    }


    public StatsDTO(Date dateCreated, String symbol, double normalDist_Volume, Time time_part, double normalDist_PRange, double pRange, long latestVolume, String name) {
        this.dateCreated = dateCreated;
        this.symbol = symbol;
        this.normalDist_Volume = normalDist_Volume;
        this.time_part = time_part;
        this.normalDist_PRange = normalDist_PRange;
        this.pRange = pRange;
        this.latestVolume = latestVolume;
        this.name = name;
    }

    @Override
    public String toString() {
        return "StatsDTO{" +
                "dateCreated=" + dateCreated +
                ", symbol='" + symbol + '\'' +
                ", normalDist_Volume=" + normalDist_Volume +
                ", time_part=" + time_part +
                ", normalDist_PRange=" + normalDist_PRange +
                ", pRange=" + pRange +
                ", latestVolume=" + latestVolume +
                ", name='" + name + '\'' +
                '}';
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getNormalDist_Volume() {
        return normalDist_Volume;
    }

    public void setNormalDist_Volume(double normalDist_Volume) {
        this.normalDist_Volume = normalDist_Volume;
    }

    public Time getTime_part() {
        return time_part;
    }

    public void setTime_part(Time time_part) {
        this.time_part = time_part;
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
}



