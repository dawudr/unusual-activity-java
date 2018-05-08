import {Time} from "@angular/common";

export class Stats {

    id: number;
    symbol: string;
    dateCreated: Date;
    time_part: string;
    normalDist_Volume: number;
    normalDist_PRange: number;
    pRange: number;
    latestVolume: number;
    name: string;


    constructor(id: number, symbol: string, dateCreated: Date, time_part: string, normalDist_Volume: number, normalDist_PRange: number, pRange: number, latestVolume: number, name: string) {
        this.id = id;
        this.symbol = symbol;
        this.dateCreated = dateCreated;
        this.time_part = time_part;
        this.normalDist_Volume = normalDist_Volume;
        this.normalDist_PRange = normalDist_PRange;
        this.pRange = pRange;
        this.latestVolume = latestVolume;
        this.name = name;
    }
}
