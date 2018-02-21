export class Stock {

  id: number;
  symbol: string;
  date: Date;
  open: number;
  close: number;
  high: number;
  low: number;
  volume: number;

  constructor(id: number, symbol: string, date: Date, open: number, close: number, high: number, low: number, volume: number){
    this.id = id;
    this.symbol = symbol;
    this.date = date;
    this.open = open;
    this.close = close;
    this.high = high;
    this.low = low;
    this.volume = volume;
  }

}
