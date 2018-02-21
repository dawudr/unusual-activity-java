import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StockRoutingModule } from './stock-routing.module';
import { StockListComponent } from './stock-list/stock-list.component';
import { StockFormComponent } from './stock-form/stock-form.component';
import { FormsModule, ReactiveFormsModule} from "@angular/forms";
import { NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';

@NgModule({
  imports: [
    CommonModule,
    StockRoutingModule,
    ReactiveFormsModule,
    FormsModule, // <-- import the FormsModule before binding with [(ngModel)]
    NgbModule.forRoot(),
    NgxDatatableModule
  ],
  declarations: [StockListComponent, StockFormComponent]
})
export class StockModule { }
