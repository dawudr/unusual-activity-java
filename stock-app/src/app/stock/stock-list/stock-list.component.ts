import { Component, OnInit, Input,ChangeDetectorRef, ViewChild} from '@angular/core';
import { Stock } from "../stock";
import { StockService } from "../stock.service";
import { Router} from "@angular/router";
import { DatatableComponent} from '@swimlane/ngx-datatable';

@Component({
  selector: 'app-stock-list',
  templateUrl: './stock-list.component.html',
  styleUrls: ['./stock-list.component.css'],
  providers: [StockService]
})
export class StockListComponent implements OnInit {

  stocks: Stock[];
  columns = [
    { prop: 'id' },
    { name: 'Symbol' },
    { name: 'Date' },
    { name: 'Volume' }
  ];
  loadingIndicator: boolean = true;
  totalRows: any = [];
  @ViewChild(DatatableComponent) table: DatatableComponent;

  constructor(private router: Router,
              private stockService: StockService,
              private _changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit() { //when component loading get all users and set the users[]
    this.stockService.findAll().subscribe(data => {
        this.stocks = data;
        this.totalRows = this.stocks;
      }
    );
  }

  getAllStocks() {
    this.stockService.findAll().subscribe(stocks => {
        return this.stocks = stocks;
      },
      err => {
        console.log(err);
      }
    );
  }

  deleteStock(id: number) {
    console.log('deleteStock:' + id);
      this.stockService.deleteStockById(id).subscribe(
        res => {
          this.getAllStocks();
        },
        err => {
          console.log(err);
        }
      )
      this.redirectStockPage();
  }

  redirectStockPage() {
    this.router.navigate(['/stock']);
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
