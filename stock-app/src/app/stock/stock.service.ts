import { Injectable } from '@angular/core';
import { Stock } from "./stock";
import { Http, Response } from "@angular/http";
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch';
import { Observable } from 'rxjs';


@Injectable()
export class StockService {

  //private apiUrl = 'http://localhost:8080/api/stock';
  //private apiUrl2 = 'http://localhost:8080/api/intraday';

  private apiUrl = 'http://moon-dragon.westeurope.cloudapp.azure.com:8080/api/stock';
  private apiUrl2 = 'http://moon-dragon.westeurope.cloudapp.azure.com:8080/api/intraday';


  constructor(private http: Http) {
  }

  findAll(): Observable<Stock[]>  {
    return this.http.get(this.apiUrl)
      .map((res:Response) => res.json())
      .catch(this.handleError);
  }

  findById(id: number): Observable<Stock> {
    return this.http.get(this.apiUrl + '/' + id)
      .map((res:Response) => res.json())
      .catch(this.handleError);
  }

  saveStock(stock: Stock): Observable<Stock> {
    console.log("saveStock Stock:[%s]", JSON.stringify(stock))
    return this.http.post(this.apiUrl, stock)
      .map((res:Response) => res.json())
      .catch(this.handleError);
  }

  deleteStockById(id: number) {
    console.log("deleteStockById ID:" + id )
    return this.http.delete(this.apiUrl + '/' + id)
  }

  updateStock(id: number, stock: Stock): Observable<Stock> {
    console.log("updateStock ID:" + id + "Stock:[%s]", JSON.stringify(stock))
    return this.http.put(this.apiUrl + '/' + id, stock)
      .map((res:Response) => res.json())
      .catch(this.handleError);
  }

  importDaily(): Observable<Stock[]> {
    console.log("importDaily Stock Feed")
    return this.http.get(this.apiUrl + '/import/start')
      .map((res:Response) => res.json())
      .catch(this.handleError);
  }

  importIntraday(): Observable<Stock[]> {
    console.log("importIntraday Stock Feed")
    return this.http.get(this.apiUrl2 + '/import/start')
      .map((res:Response) => res.json())
      .catch(this.handleError);
  }

  private extractData(res: Response) {
    let body = res.json();
    return body || {};
  }

  private handleError(error: any) {
    let errMsg = (error.message) ? error.message :
      error.status ? `${error.status} - ${error.statusText}` : 'Server error';
    console.error(errMsg);
    return Observable.throw(errMsg);
  }
}
