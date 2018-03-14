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
  timeAgo: string = moment().format('MMMM Do YYYY, h:mm:ss a');
  stocks: Stock[];
  stocksh: Stock[];

  // Toastr Params
  options: GlobalConfig;
  title = 'Starting notifications';
  message = 'Begin...';
  type = types[1];
  version = VERSION;
  enableBootstrap = true;
  progressBar = true;
  extendedTimeOut = 1000;
  easing = 'ease-in';
  progressAnimation = 'decreasing';

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
              this.title = "Volume Spike Detected:"
              this.message = "Symbol: " + this.stocks[i].symbol + " Volume: " + this.stocks[i].volume + " at " + new Date(this.stocks[i].date);
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
                this.title = "Volume Spike Detected:"
                this.message = "Symbol: " + this.stocks[i].symbol + " Volume: " + this.stocks[i].volume + " at " + new Date(this.stocks[i].date);
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
}
