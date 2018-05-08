import {Component, OnInit, ViewChild, Renderer2, VERSION, ChangeDetectorRef, OnDestroy} from '@angular/core';
import * as moment from 'moment';
import {ToastContainerDirective, ToastrService} from 'ngx-toastr';
import { Stats } from "../stats";

import {
  GlobalConfig,
  ToastNoAnimation,
} from '../../../lib/public_api';

import { cloneDeep, random } from 'lodash-es';
import {Router, ActivatedRoute, ParamMap } from "@angular/router";
import {NotificationsService} from "../notifications.service";
import {Subscription} from "rxjs/Subscription";
import {Subject} from "rxjs/Subject";
import 'rxjs/add/operator/takeUntil';
import {DatatableComponent} from "@swimlane/ngx-datatable";
import { switchMap } from 'rxjs/operators';

// See https://stackoverflow.com/questions/44947551/angular2-4-refresh-data-realtime
// https://stackoverflow.com/questions/38008334/angular-rxjs-when-should-i-unsubscribe-from-subscription/41177163#41177163
// https://github.com/scttcper/ngx-toastr

interface Quote {
  title?: string;
  message?: string;
}

const quotes: Quote[] = [
  {
    title: 'Title',
    message: 'Message',
  },
  {
    title: '😃',
    message: 'Supports Emoji',
  },
  {
    message: 'My name is Inigo Montoya. You killed my father. Prepare to die!',
  },
  {
    message: 'Titles are not always needed',
  },
  {
    title: 'Title only 👊',
  },
  {
    title: '',
    message: `Supports Angular ${VERSION.full}`,
  },
];
const types = ['success', 'error', 'info', 'warning'];




@Component({
  selector: 'app-stock-notifications',
  templateUrl: './stock-notifications.component.html',
  styleUrls: ['./stock-notifications.component.css'],
  providers: [NotificationsService]
})
export class StockNotificationsComponent implements OnInit, OnDestroy {

  private subscription: Subscription = new Subscription();
  private unsubscribe: Subject<Stats> = new Subject();

  data: any;
  interval: any;


  momentAgo: string = moment().startOf('minute').fromNow();
  timeAgo: string = moment().format('MMMM Do YYYY, h:mm');
  stocks: Stats[];
  stocksh: Stats[];
  private symbol: string;
  private ndvol: number;
  private ndprange: number;
  private time: number = 1;
  private realtime: boolean = true;

  // Toastr Params
  options: GlobalConfig;
  title = 'Starting notifications';
  message = 'Begin...';
  type = types[1];
  version = VERSION;
  enableBootstrap = true;
  progressBar = true;
  extendedTimeOut = 150000;
  easing = '30000';
  progressAnimation = 'decreasing';
  timeOut = 30000;

  loadingIndicator: boolean = true;
  totalRows: any = [];
  @ViewChild(DatatableComponent) table: DatatableComponent;

  private lastInserted: number[] = [];

  @ViewChild(ToastContainerDirective) toastContainer: ToastContainerDirective;


  constructor(private router: Router,
              private notificationsService: NotificationsService,
              private _changeDetectorRef: ChangeDetectorRef,
              public toastr: ToastrService,
              private renderer: Renderer2,
              private route: ActivatedRoute) {
    this.options = this.toastr.toastrConfig;

  }

  ngOnInit() {
    this.toastr.overlayContainer = this.toastContainer;

    this.route.params.subscribe(params => {
      this.symbol=params['symbol'];
      this.ndvol=params['ndvol'];
      this.ndprange=params['ndprange'];
      this.realtime=params['realtime'];
    });

    // this.notificationsService.getNotifications().subscribe(data => {
    //     this.stocks = data;
    //   }
    // );

    // this.notificationsService.getHistory().subscribe(data => {
    //     this.stocksh = data;
    //   }
    // );
/*

    this.refreshData();
    this.interval = setInterval(() => {
      this.refreshData();
    }, 50000);

*/


    this.refreshData();
    if(this.interval){
      clearInterval(this.interval);
    }
    this.interval = setInterval(() => {
      this.refreshData();
    }, 60000);


    this.notificationsService.data$.takeUntil(this.unsubscribe)
      .subscribe(data => {
        this.data = data;
      });

  }

  onClick() {
    this.toastr.success('in div');
  }


  ngOnDestroy() {
    // this.subscription.unsubscribe();
    // clearInterval(this.interval);

    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  refreshData(){
    // this.subscription.add(
      this.notificationsService.getNotifications(this.symbol, this.realtime)
        .subscribe(data => {
          this.stocks = data;

          if(this.stocks.length > 0) {
            for (var i = 0; i < this.stocks.length; i++) {
                this.title = "Volume spike of: " + this.stocks[i].latestVolume + " Percentile: " + this.stocks[i].normalDist_Volume + "% - \n " + moment(new Date(this.stocks[i].time_part), "h:mm ZZ").fromNow() + ")";
                this.message = this.stocks[i].symbol + " - " + this.stocks[i].name + " Price Range: " + this.stocks[i].normalDist_PRange;
                if(this.stocks[i].latestVolume > 0) {
                  this.type = types[0]
              } else {
                  this.type = types[1]
              }
              this.openToast();
            }
            //this.refreshHistory();
          }
        })
    // );

    this.notificationsService.getNotifications(this.symbol, this.realtime)
      .takeUntil(this.unsubscribe)
      .subscribe();
  }


  doAction(){
    this.subscription.add(
      this.notificationsService.getNotifications(this.symbol, this.realtime)
        .subscribe(data => {
          if(data.length > 0){
            this.stocks = data;

            if(this.stocks.length > 0) {
              for (var i = 0; i < this.stocks.length; i++) {
                  this.title = "Volume spike of: " + this.stocks[i].latestVolume + " Percentile: " + this.stocks[i].normalDist_Volume + "% - \n " + moment(new Date(this.stocks[i].time_part), "h:mm ZZ").fromNow() + ")";
                  this.message = this.stocks[i].symbol + " - " + this.stocks[i].name + " Price Range: " + this.stocks[i].normalDist_PRange;
                this.openToast();
              }
              this.refreshHistory();
            }
            this.refreshData();
          }
        })
    );
  }

  refreshHistory() {
    // this.notificationsService.getNotifications().subscribe(data => {
    //     this.stocks = data;
    //   }
    // );

    this.notificationsService.getNotifications(this.symbol, this.realtime).subscribe(data => {
        this.stocksh = data;
      }
    );

  }

  // showSuccess() {
  //   this.toastrService.success('Hello world!', 'Toastr fun!');
  // }

  getMessage() {
    let m: string | undefined = this.message;
    let t: string | undefined = this.title;
    if (!this.title.length && !this.message.length) {
      const randomMessage = quotes[random(0, quotes.length - 1)];
      m = randomMessage.message;
      t = randomMessage.title;
    }
    return {
      message: m,
      title: t,
    };
  }
  openToast() {
    const { message, title } = this.getMessage();
    // Clone current config so it doesn't change when ngModel updates
    const opt = cloneDeep(this.options);
    const inserted = this.toastr.show(
      message,
      title,
      opt,
      this.options.iconClasses[this.type],
    );
    if (inserted) {
      this.lastInserted.push(inserted.toastId);
    }
    return inserted;
  }
  openToastNoAnimation() {
    const { message, title } = this.getMessage();
    const opt = cloneDeep(this.options);
    // opt.toastComponent = ToastNoAnimation;
    const inserted = this.toastr.show(
      message,
      title,
      opt,
      this.options.iconClasses[this.type],
    );
    if (inserted) {
      this.lastInserted.push(inserted.toastId);
    }
    return inserted;
  }

  clearToasts() {
    this.toastr.clear();
    this.refreshHistory()
  }
  clearLastToast() {
    this.toastr.clear(this.lastInserted.pop());
  }
  fixNumber(field: string) {
    this.options[field] = Number(this.options[field]);
  }
  setClass(enableBootstrap: boolean) {
    const add = enableBootstrap ? 'bootstrap' : 'normal';
    const remove = enableBootstrap ? 'normal' : 'bootstrap';
    this.renderer.addClass(document.body, add);
    this.renderer.removeClass(document.body, remove);
  }

  filterData(event) {
      console.log(event);
      let columnName = event.currentTarget.id;
      const val = event.target.value.toLowerCase();
      const filteredData = this.totalRows.filter(function (d) {
          return d[columnName].toLowerCase().indexOf(val) !== -1 || !val;
      });
      this.stocks = filteredData;
      this.table.offset = 0;
  }

}
