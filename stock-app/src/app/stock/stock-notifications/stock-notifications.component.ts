import {Component, OnInit, ViewChild, Renderer2, VERSION, ChangeDetectorRef, OnDestroy} from '@angular/core';
import {ToastContainerDirective, ToastrService} from 'ngx-toastr';
import {Stats} from "../stats";
import {GlobalConfig, ToastNoAnimation} from '../../../lib/public_api';

import {cloneDeep, random} from 'lodash-es';
import {Router, ActivatedRoute, ParamMap, Params} from "@angular/router";
import {NotificationsService} from "../notifications.service";
import {Subscription} from "rxjs/Subscription";
import {Subject} from "rxjs/Subject";
import 'rxjs/add/operator/takeUntil';
import {DatatableComponent} from "@swimlane/ngx-datatable";
import {switchMap} from 'rxjs/operators';
import {FilterPipe} from '../../filter.pipe';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';
import {SymbolService} from "../../symbol/symbol.service";
import {Symbol} from "../../symbol/symbol";
import {StockService} from "../stock.service";

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
    providers: [NotificationsService, SymbolService],
})
export class StockNotificationsComponent implements OnInit, OnDestroy {

    private subscription: Subscription = new Subscription();
    private unsubscribe: Subject<Stats> = new Subject();

    data: any;
    interval: any;

    public form1: FormGroup;
    // momentAgo: string = moment().startOf('minute').fromNow();
    // timeAgo: string = moment().format('MMMM Do YYYY, h:mm');
    stocks: Stats[];
    symbols: Symbol[];
    symbol: string;
    ndvol: number = 95;
    ndprange: number = 30;
    time: number = 20;
    realtime: boolean = true;

    // Toastr Params
    options: GlobalConfig;
    title = 'Starting notifications';
    message = 'Begin...';
    type = types[1];
    version = VERSION;
    enableBootstrap = true;
    progressBar = true;
    extendedTimeOut = 150000;
    easing = 'ease-in';
    easeTime = '1000'
    progressAnimation = 'decreasing';
    timeOut = 30000;
    maxOpened: 10;

    loadingIndicator: boolean = true;
    totalRows: any = [];
    @ViewChild(DatatableComponent) table: DatatableComponent;

    private lastInserted: number[] = [];

    @ViewChild(ToastContainerDirective) toastContainer: ToastContainerDirective;


    constructor(private router: Router,
                private notificationsService: NotificationsService,
                private symbolService: SymbolService,
                private _changeDetectorRef: ChangeDetectorRef,
                public toastr: ToastrService,
                private renderer: Renderer2,
                private route: ActivatedRoute,
                private formBuilder: FormBuilder,
                private modalService: NgbModal) {
        this.options = this.toastr.toastrConfig;

    }

    ngOnInit() {
        this.toastr.overlayContainer = this.toastContainer;

        this.route.params.subscribe((params: Params) => {
            this.symbol = params['symbol'];
        });

        this.route.queryParams.subscribe(params => {
            this.ndvol = params["ndvol"] || this.ndvol;
            this.ndprange = params["ndprange"] || this.ndprange;
            this.time = params["time"] || this.time;
        });

        this.form1 = this.formBuilder.group({'single': [10]});

        this.getAllSymbols();

        this.refreshData();
        if (this.interval) {
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


    getAllSymbols() {
        this.symbolService.findAll().subscribe(data => {
                return this.symbols = data;
            },
            err => {
                console.log(err);
            }
        );
    }

    onSymbolChange() {
        console.log('Symbol Changed: '+ this.symbol);
        this.refreshData();
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

    refreshData() {
        // this.subscription.add(
        this.notificationsService.getNotifications(this.symbol, this.time, this.realtime, this.ndvol, this.ndprange)
            .subscribe(data => {
                this.stocks = data;

                // Break up news JSON into formatted string
                this.stocks.forEach(function (value, index, array) {
                    if(value.news && value.news != "") {
                        var news = JSON.parse(value.news)[0];
                        value.headline = news.headline;
                        value.source = news.source;
                        value.url = news.url;
                        value.datetime = news.datetime;
                        value.summary = news.summary;
                        value.related = news.related;
                    }
                })

                if (this.stocks.length > 0) {
                    for (var i = 0; i < this.stocks.length; i++) {
                        this.title = this.stocks[i].symbol + " (" + this.stocks[i].name + ") " + this.stocks[i].time_part + "(ET)";
                        this.message = "VOL:" + this.stocks[i].latestVolume + " PR: " + this.stocks[i].pRange.toFixed(2);
                        if (this.stocks[i].latestVolume > 0) {
                            this.type = types[0]
                        } else {
                            this.type = types[1]
                        }
                        this.openToast();
                    }
                }
            })

        this.notificationsService.getNotifications(this.symbol, this.time, this.realtime, this.ndvol, this.ndprange)
            .takeUntil(this.unsubscribe)
            .subscribe();
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
        const {message, title} = this.getMessage();
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
        const {message, title} = this.getMessage();
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

    closeResult: string;

    open(content) {
        window.open(content);

/*        this.modalService.open(content).result.then((result) => {
            this.closeResult = `Closed with: ${result}`;
        }, (reason) => {
            this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
        });*/
    }

    private getDismissReason(reason: any): string {
        if (reason === ModalDismissReasons.ESC) {
            return 'by pressing ESC';
        } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
            return 'by clicking on a backdrop';
        } else {
            return  `with: ${reason}`;
        }
    }

    isMarketClosed() {
        var objDate = new Date(); //.toLocaleString("en-US", {timeZone: "America/New_York"});
        var currtime = objDate.getHours() * 100 + objDate.getMinutes();
        if (currtime > 930 && currtime < 1630){
            // closed between 20:00 (8 pm) and 8:00 (8 am) as an example
            return false;
        } else {
            return true;
        }
    }




}
