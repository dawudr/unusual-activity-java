import { BrowserModule } from '@angular/platform-browser';
import {NgModule, VERSION} from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { StockModule } from './stock/stock.module';
import { HttpModule } from '@angular/http';
import { AppComponent } from './app.component';
import { ToastrModule, ToastContainerModule } from 'ngx-toastr';
import { FeaturesComponent } from './features/features.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  declarations: [
    AppComponent,
    FeaturesComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    StockModule,
    HttpModule,
    ToastrModule.forRoot({
      timeOut: 3000,
      positionClass: 'toast-top-right',
      preventDuplicates: true,
      progressBar: true,
      extendedTimeOut: 1000,
      easing: 'ease-in',
      progressAnimation: 'decreasing',
      tapToDismiss: true,
      maxOpened: 10,
      autoDismiss: true
    }),
    ToastContainerModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
