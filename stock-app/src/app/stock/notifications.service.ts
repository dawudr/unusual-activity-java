import { Injectable } from '@angular/core';
import {Http, Response} from "@angular/http";
import { Observable, BehaviorSubject } from 'rxjs';
import {API_HOST} from "../../constants";
import {Stats} from "./stats";

@Injectable()
export class NotificationsService {

  private apiUrl = API_HOST + '/api/stats/data';

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

  getNotifications(symbol :string, time: number, realtime: boolean, ndvol: number, ndprange: number): Observable<Stats[]>  {
    var requesturl = "";
    if(symbol) {
        this.requesturl= this.apiUrl + "/" + symbol;
    } else {
        this.requesturl= this.apiUrl;
    }

    if(!realtime) {
        this.requesturl = this.requesturl.concat("?realtime=false")
    } else {
        this.requesturl = this.requesturl.concat('?realtime=true');
    }


    if(time > 0 || !realtime || ndvol > 0 || ndprange > 0) {
        if(time > 2) {
            this.requesturl = this.requesturl.concat('&time=' + time);
        }
    }


    if(ndvol > 0 || ndprange > 0) {

        if(ndvol) {
            this.requesturl= this.requesturl.concat('&ndvol=' + ndvol);
        }

        if(ndprange) {
            this.requesturl= this.requesturl.concat('&ndprange=' + ndprange);
        }

    }

    console.log("URL=" + this.requesturl);
    return this.http.get(this.requesturl)
      .map((res:Response) => res.json())
      .catch(this.handleError);
  }


  getVolumesToday(symbol :string): Observable<Stats[]>  {
      var requesturl = API_HOST + '/api/stats/volumestoday/' + symbol;

      console.log("URL Request=" + requesturl);

      return this.http.get(requesturl)
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
