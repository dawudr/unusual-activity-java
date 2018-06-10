package com.financialjuice.unusualactivity.model;

import java.sql.Time;

public class ChartSeries {

    private Time time_part;
    private Long volume;
    private Double pRange;

    public ChartSeries(Time time_part, Long volume, Double pRange) {
        this.time_part = time_part;
        this.volume = volume;
        this.pRange = pRange;
    }

    public Time getTime_part() {
        return time_part;
    }

    public void setTime_part(Time time_part) {
        this.time_part = time_part;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public Double getpRange() {
        return pRange;
    }

    public void setpRange(Double pRange) {
        this.pRange = pRange;
    }
}
