import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StockRoutingModule } from './stock-routing.module';
import { StockListComponent } from './stock-list/stock-list.component';
import { StockFormComponent } from './stock-form/stock-form.component';
import { FormsModule, ReactiveFormsModule} from "@angular/forms";
import { NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { StockChartComponent } from './stock-chart/stock-chart.component';
import { NvD3Module } from 'ng2-nvd3';
import { StockNotificationsComponent } from './stock-notifications/stock-notifications.component';
import { MomentModule } from 'angular2-moment';
import { MomentTimezoneModule } from 'angular-moment-timezone';
import { SplitPipe} from '../split.pipe';
import {StockSparklineComponent} from "./stock-sparkline/stock-sparkline.component";

@NgModule({
  imports: [
    CommonModule,
    StockRoutingModule,
    ReactiveFormsModule,
    FormsModule, // <-- import the FormsModule before binding with [(ngModel)]
    NgbModule.forRoot(),
    NgxDatatableModule,
    NvD3Module,
    MomentModule,
    MomentTimezoneModule,
  ],
  declarations: [StockListComponent, StockFormComponent, StockChartComponent, StockNotificationsComponent, SplitPipe, StockSparklineComponent
  ]
})
export class StockModule { }
