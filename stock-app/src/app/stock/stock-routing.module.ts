import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StockListComponent } from './stock-list/stock-list.component';
import { StockFormComponent } from './stock-form/stock-form.component';

const routes: Routes = [
  {path: 'stock', component: StockListComponent},
  {path: 'stock/create', component: StockFormComponent},
  {path: 'stock/edit/:id', component: StockFormComponent},
  {path: 'stock/delete/:id', component: StockListComponent}
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class StockRoutingModule { }
