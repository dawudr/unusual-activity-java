import { Injectable } from '@angular/core';
import {API_HOST} from "../../constants";
import {Http, Response} from "@angular/http";
import {Observable} from "rxjs/Rx";
import {Stock} from "../stock/stock";

@Injectable()
export class SymbolService {

    private apiUrl = API_HOST + '/api/symbol';

    constructor(private http: Http) {
    }

    findAll(): Observable<Stock[]>  {
        return this.http.get(this.apiUrl)
            .map((res:Response) => res.json())
            .catch(this.handleError);
    }

    findBySymbol(symbol: string): Observable<Stock> {
        return this.http.get(this.apiUrl + '/' + symbol)
            .map((res:Response) => res.json())
            .catch(this.handleError);
    }

    private handleError(error: any) {
        let errMsg = (error.message) ? error.message :
            error.status ? `${error.status} - ${error.statusText}` : 'Server error';
        console.error(errMsg);
        return Observable.throw(errMsg);
    }
}