import { Injectable } from '@angular/core';
import {Http, Response} from "@angular/http";
import {Observable} from "rxjs/Observable";

@Injectable()
export class ChartService {

  private apiUrl = 'http://localhost:8080/api/stats';

  constructor(private http: Http) {
  }

  getBoxPlot(): Observable<Array<Map<string, Object>>>  {
    return this.http.get(this.apiUrl + '/chartseries')
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
