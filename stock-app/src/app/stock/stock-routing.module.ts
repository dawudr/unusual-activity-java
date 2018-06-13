import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StockListComponent } from './stock-list/stock-list.component';
import { StockFormComponent } from './stock-form/stock-form.component';
import {StockChartComponent} from "./stock-chart/stock-chart.component";
import {StockNotificationsComponent} from "./stock-notifications/stock-notifications.component";
import {FeaturesComponent} from "../features/features.component";

const routes: Routes = [
  {path: 'stock', component: StockListComponent},
  {path: 'stock/create', component: StockFormComponent},
  {path: 'stock/edit/:id', component: StockFormComponent},
  {path: 'stock/delete/:id', component: StockListComponent},
  {path: 'stock/chart', component: StockChartComponent},
  {path: 'stock/notify', component: StockNotificationsComponent},
  {path: 'stock/notify/:symbol', component: StockNotificationsComponent},
  {path: 'features', component: FeaturesComponent},
  {path: '', component: StockNotificationsComponent}
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class StockRoutingModule { }
