export class Symbol {

    symbol: string;
    name: string;
    startdate: Date;
    instrumentname: string;
    market: string;
    datatype: string;
    industry: string;
    sector: string;
    country: string;
    currency: string;
    marketcap: string;
    fcacategory: string;

    constructor(symbol: string, name: string, startdate: Date, instrumentname: string, market: string, datatype: string, industry: string, sector: string, country: string, currency: string, marketcap: string, fcacategory: string) {
        this.symbol = symbol;
        this.name = name;
        this.startdate = startdate;
        this.instrumentname = instrumentname;
        this.market = market;
        this.datatype = datatype;
        this.industry = industry;
        this.sector = sector;
        this.country = country;
        this.currency = currency;
        this.marketcap = marketcap;
        this.fcacategory = fcacategory;
    }
}
