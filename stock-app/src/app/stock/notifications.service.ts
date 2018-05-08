import { Injectable } from '@angular/core';
import {Http, Response} from "@angular/http";
import { Observable, BehaviorSubject } from 'rxjs';
import {API_HOST} from "../../constants";
import {Stats} from "./stats";

@Injectable()
export class NotificationsService {

  private apiUrl = API_HOST + '/api/alerts/data';

  private dataSubject: BehaviorSubject<Stats[]> = new BehaviorSubject([]);

  data$: Observable<Stats[]> = this.dataSubject.asObservable();
  private requesturl: string;

  updateData(): Observable<any>  {
    return this.getData().do((data) => {
      this.dataSubject.next(data);
    });
  }

    // My data is an array of model objects
    getData(): Observable<Stats[]>{
    return this.http.get(this.apiUrl)
      .map((response: Response) => {
        let data = response.json() && response.json().your_data_objects;
        if(data){
          return data;
        }
      })
  }


  constructor(private http: Http) {
  }

  getNotifications(symbol :string, realtime: boolean): Observable<Stats[]>  {
    this.requesturl = "";
    if(symbol) {
        this.requesturl= this.apiUrl + "/" + symbol;
    } else {
        this.requesturl= this.apiUrl;
    }

    if(!realtime) {
      this.requesturl = this.requesturl.concat('?realtime=false');
    }

    return this.http.get(this.requesturl)
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
