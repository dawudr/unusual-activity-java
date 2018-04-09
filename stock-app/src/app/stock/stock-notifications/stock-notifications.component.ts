import {Component, OnInit, ViewChild, Renderer2, VERSION, ChangeDetectorRef, OnDestroy} from '@angular/core';
import * as moment from 'moment';
import {ToastContainerDirective, ToastrService} from 'ngx-toastr';
import { Stock } from "../stock";

import {
  GlobalConfig,
  ToastNoAnimation,
} from '../../../lib/public_api';

import { cloneDeep, random } from 'lodash-es';
import {Router} from "@angular/router";
import {NotificationsService} from "../notifications.service";
import {Subscription} from "rxjs/Subscription";
import {Subject} from "rxjs/Subject";
import 'rxjs/add/operator/takeUntil';
import {Alert} from "../alert";
import {DatatableComponent} from "@swimlane/ngx-datatable";

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
  private unsubscribe: Subject<Stock> = new Subject();

  data: any;
  interval: any;


  momentAgo: string = moment().startOf('minute').fromNow();
  timeAgo: string = moment().format('MMMM Do YYYY, h:mm');
  stocks: Alert[];
  stocksh: Alert[];

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


  columns = [
      { prop: 'volumeChange' },
      { prop: 'stockData.symbol' },
      { prop: 'stockData.name' },
      { prop: 'volumeChangePct' },
      { prop: 'volumeChange' },
      { prop: 'stockData.date' },
      { prop: 'stockData' }
  ];


  columnWidths = [
      {column: "volumeChange", width: 10},
      {column: "stockData.symbol", width: 10},
      {column: "stockData.name", width: 30},
      {column: "volumeChangePct", width: 10},
      {column: "volumeChange", width: 10},
      {column: "stockData.date", width: 10},
      {column: "stockData", width: 50}
  ]


  loadingIndicator: boolean = true;
  totalRows: any = [];
  @ViewChild(DatatableComponent) table: DatatableComponent;

  private lastInserted: number[] = [];

  @ViewChild(ToastContainerDirective) toastContainer: ToastContainerDirective;


  constructor(private router: Router,
              private notificationsService: NotificationsService,
              private _changeDetectorRef: ChangeDetectorRef,
              public toastr: ToastrService,
              private renderer: Renderer2) {
    this.options = this.toastr.toastrConfig;

  }

  ngOnInit() {
    this.toastr.overlayContainer = this.toastContainer;
    this.columns.forEach((col: any) => {
        const colWidth = this.columnWidths.find(colWidth => colWidth.column === col.prop);
        if (colWidth) {
            col.width = colWidth.width;
        }
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
    }, 30000);


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
      this.notificationsService.getNotifications()
        .subscribe(data => {
          this.stocks = data;

          if(this.stocks.length > 0) {
            for (var i = 0; i < this.stocks.length; i++) {
                this.title = "Volume spike: " + this.stocks[i].volumeChangePct + "% (" + this.stocks[i].volumeChange + ") - \n " + moment(new Date(this.stocks[i].stockData.date), "MMM Do, YYYY, h:mm ZZ").fromNow() + ")";
                this.message = this.stocks[i].stockData.symbol + " - " + this.stocks[i].stockData.name;
                if(this.stocks[i].volumeChange > 0) {
                  this.type = types[0]
              } else {
                  this.type = types[1]
              }
              this.openToast();
            }
            this.refreshHistory();
          }
        })
    // );

    this.notificationsService.getNotifications()
      .takeUntil(this.unsubscribe)
      .subscribe();
  }


  doAction(){
    this.subscription.add(
      this.notificationsService.getNotifications()
        .subscribe(data => {
          if(data.length > 0){
            this.stocks = data;

            if(this.stocks.length > 0) {
              for (var i = 0; i < this.stocks.length; i++) {
                  this.title = "Volume spike: " + this.stocks[i].volumeChangePct + "% (" + this.stocks[i].volumeChange + ") - \n " + moment(new Date(this.stocks[i].stockData.date), "MMM Do, YYYY, h:mm ZZ").fromNow() + ")";
                  this.message = this.stocks[i].stockData.symbol + " - " + this.stocks[i].stockData.name;
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

    this.notificationsService.getHistory().subscribe(data => {
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
