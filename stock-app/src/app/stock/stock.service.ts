import { Injectable } from '@angular/core';
import { Stock } from "./stock";
import { Http, Response } from "@angular/http";
import 'rxjs/add/operator/map'
import 'rxjs/add/operator/catch';
import { Observable } from 'rxjs/Observable';


@Injectable()
export class StockService {

  private apiUrl = 'http://localhost:8080/api/stock';

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
