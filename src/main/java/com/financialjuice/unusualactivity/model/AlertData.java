package com.financialjuice.unusualactivity.model;

import com.financialjuice.unusualactivity.dto.StockDataDTO;

@Deprecated
public class AlertData {

    private long volumeChange;
    private double volumeChangePct;
    private String triggerNews;
    private StockDataDTO stockData;

    public AlertData(StockDataDTO stock, long volumeChange, double volumeChangePct, String triggerNews) {
        this.volumeChange = volumeChange;
        this.volumeChangePct = volumeChangePct;
        this.triggerNews = triggerNews;
        this.stockData = stock;
    }

    public long getVolumeChange() {
        return volumeChange;
    }

    public void setVolumeChange(Long volumeChange) {
        this.volumeChange = volumeChange;
    }

    public double getVolumeChangePct() {
        return volumeChangePct;
    }

    public void setVolumeChangePct(double volumeChangePct) {
        this.volumeChangePct = volumeChangePct;
    }

    public String getTriggerNews() {
        return triggerNews;
    }

    public void setTriggerNews(String triggerNews) {
        this.triggerNews = triggerNews;
    }

    public StockDataDTO getStockData() {
        return stockData;
    }

    public void setStockData(StockDataDTO stockData) {
        this.stockData = stockData;
    }
}
