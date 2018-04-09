import {Stock} from "./stock";

export class Alert {

  volumeChange: number;
  volumeChangePct: number;
  stockData: Stock;

  constructor(volumeChange: number, volumeChangePct: number, stockData: Stock){
    this.volumeChange = volumeChange;
    this.volumeChangePct = volumeChangePct;
    this.stockData = stockData;
  }

}
