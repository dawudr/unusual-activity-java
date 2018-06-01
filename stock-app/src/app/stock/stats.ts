import {Time} from "@angular/common";

export class Stats {

    date: Date;
    symbol: string;
    normalDist_Volume: number;
    normalDist_PRange: number;
    pRange: number;
    latestVolume: number;
    name: string;
    date_part: Date;
    time_part: string;
    news: string;
    headline: string;
    source: string;
    url: string;
    datetime: string;
    summary: string;
    related: string;
    volumes: any;

    constructor(date: Date, symbol: string,
                normalDist_Volume: number, normalDist_PRange: number,
                latestVolume: number, pRange: number,
                name: string,
                date_part: Date, time_part: string, news: string) {
        this.date = date;
        this.symbol = symbol;
        this.normalDist_Volume = normalDist_Volume;
        this.normalDist_PRange = normalDist_PRange;
        this.pRange = pRange;
        this.latestVolume = latestVolume;
        this.name = name;
        this.date_part = date_part;
        this.time_part = time_part;
        this.news = news;
    }
}
