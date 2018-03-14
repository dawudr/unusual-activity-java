import {Component, OnInit} from '@angular/core';
import {StockService} from "../stock/stock.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-features',
  templateUrl: './features.component.html',
  styleUrls: ['./features.component.css'],
  providers: [StockService]

})
export class FeaturesComponent implements OnInit {

  constructor(private router: Router,
              private stockService: StockService) {
  }

  ngOnInit() {
  }

  importDaily() {
    this.stockService.importDaily()
    this.redirectStockPage();
  }

  importIntraday() {
    this.stockService.importIntraday()
    this.redirectStockPage();
  }

  redirectStockPage() {
    this.router.navigate(['/features']);
  }

}
