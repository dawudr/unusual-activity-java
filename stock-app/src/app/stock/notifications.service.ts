import { Injectable } from '@angular/core';
import {Http, Response} from "@angular/http";
import { Observable, BehaviorSubject } from 'rxjs';
import {Stock} from "./stock";

@Injectable()
export class NotificationsService {

  // private apiUrl = 'http://localhost:8080/api/notify';
  private apiUrl = 'http://evening-plateau-95719.herokuapp.com/api/notify';

  private dataSubject: BehaviorSubject<Stock[]> = new BehaviorSubject([]);

  data$: Observable<Stock[]> = this.dataSubject.asObservable();

  updateData(): Observable<any>  {
    return this.getData().do((data) => {
      this.dataSubject.next(data);
    });
  }

  // My data is an array of model objects
  getData(): Observable<Stock[]>{
    return this.http.get(this.apiUrl + '/data')
      .map((response: Response) => {
        let data = response.json() && response.json().your_data_objects;
        if(data){
          return data;
        }
      })
  }


  constructor(private http: Http) {
  }

  getNotifications(): Observable<Stock[]>  {
    return this.http.get(this.apiUrl + '/data')
      .map((res:Response) => res.json())
      .catch(this.handleError);
  }

  getHistory(): Observable<Stock[]>  {
    return this.http.get(this.apiUrl + '/history')
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
