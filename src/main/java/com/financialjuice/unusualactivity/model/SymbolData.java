package com.financialjuice.unusualactivity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "SymbolData")
@Table(name = "symboldata")
public class SymbolData extends AuditData {

    @Id
    @NotBlank
    private String symbol;
    private String name;
    private String instrumentname; // ORD 10P
    private String exchange; // LSE
    private String market; // MAIN MARKET or AIM
    private String datatype; // Equities
    private String industry; // Financials
    private String sector; // Financial Services
    private String startdate; //dd/mm/yyyy
    private String country; // United Kingdom
    private String currency; // GBX
    private String marketcap; // £42.19 in £m
    private String fcacategory; // Premium Equity Commercial Companies

    public SymbolData() {
    }

    public SymbolData(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }

    public SymbolData(String symbol, String name, String startdate, String datatype) {
        this.symbol = symbol;
        this.name = name;
        this.startdate = startdate;
        this.datatype = datatype;
    }

    public SymbolData(String symbol, String name, String instrumentname, String exchange, String market, String datatype, String industry, String sector, String startdate, String country, String currency, String marketcap, String fcacategory) {
        this.symbol = symbol;
        this.name = name;
        this.instrumentname = instrumentname;
        this.exchange = exchange;
        this.market = market;
        this.datatype = datatype;
        this.industry = industry;
        this.sector = sector;
        this.startdate = startdate;
        this.country = country;
        this.currency = currency;
        this.marketcap = marketcap;
        this.fcacategory = fcacategory;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstrumentname() {
        return instrumentname;
    }

    public void setInstrumentname(String instrumentname) {
        this.instrumentname = instrumentname;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMarketcap() {
        return marketcap;
    }

    public void setMarketcap(String marketcap) {
        this.marketcap = marketcap;
    }

    public String getFcacategory() {
        return fcacategory;
    }

    public void setFcacategory(String fcacategory) {
        this.fcacategory = fcacategory;
    }

    @Override
    public String toString() {
        return "SymbolData{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", instrumentname='" + instrumentname + '\'' +
                ", exchange='" + exchange + '\'' +
                ", market='" + market + '\'' +
                ", datatype='" + datatype + '\'' +
                ", industry='" + industry + '\'' +
                ", sector='" + sector + '\'' +
                ", startdate='" + startdate + '\'' +
                ", country='" + country + '\'' +
                ", currency='" + currency + '\'' +
                ", marketcap='" + marketcap + '\'' +
                ", fcacategory='" + fcacategory +
                '}';
    }
}
