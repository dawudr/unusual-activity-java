import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {StockService} from "../stock.service";
import {Stock} from "../stock";
import {ActivatedRoute, Router} from '@angular/router';
import {DatePipe} from '@angular/common';

/**
 * FormControl is a class that powers an individual form control, tracks the value and validation status, whilst offering a wide set of public API methods.
 * FormGroup is a group of FormControl instances, also keeps track of the value and validation status for the said group, also offers public APIs.
 * Validators from @angular/forms and pass them in as a second argument to our FormControl instances
 * Rule: need multiple Validators per FormControl? Use an array to contain them.
 * Tip: ?.prop is called the “Safe navigation operator
 * Tip: The touched property becomes true once the user has blurred the input, which may be a relevant time to show the error if they’ve not filled anything out
 */


@Component({
  selector: 'app-stock-create',
  templateUrl: './stock-form.component.html',
  styleUrls: ['./stock-form.component.css'],
  providers: [StockService]
})
export class StockFormComponent implements OnInit, OnDestroy {

  id: number;
  stock: Stock;
  stockForm: FormGroup;

  private sub: any;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private stockService: StockService,
              private formBuilder: FormBuilder) {
  }

  ngOnInit() {

    this.sub = this.route.params.subscribe(params => {
      this.id = params['id'];
      console.log("Edit Stock ID:" + this.id);
    });

    let dp = new DatePipe(navigator.language);
    let format = 'yyyy-MM-ddTHH:mm';


    //refactor to use FormBuilder as parent of FormControl and FormGroup
    this.stockForm = this.formBuilder.group({
      symbol: new FormControl('', [Validators.required, Validators.minLength(2)]),
      date: new FormControl(dp.transform(new Date(), format ), Validators.required),
      open: new FormControl('', Validators.required),
      close: new FormControl('', Validators.required),
      high: new FormControl('', Validators.required),
      low: new FormControl('', Validators.required),
      volume: new FormControl('', Validators.required)
    });


    // use patchValue with Firebase. See https://toddmotto.com/angular-2-form-controls-patch-value-set-value
    if (this.id) { //edit form
      this.stockService.findById(this.id).subscribe(
        stock => {
          this.id = stock.id;
          let dtr = dp.transform(new Date(stock.date), format );

          this.stockForm.patchValue({
            symbol: stock.symbol,
            date: dtr,
            open: stock.open,
            close: stock.close,
            high: stock.high,
            low: stock.low,
            volume: stock.volume,
          });
        }, error => {
          console.log(error);
        }
      );
    }
  }

  onSubmit() {
    if (this.stockForm.valid) {
      if (this.id) {
        let stock: Stock = new Stock(this.id,
          this.stockForm.controls['symbol'].value,
          new Date(this.stockForm.controls['date'].value),
          this.stockForm.controls['open'].value,
          this.stockForm.controls['close'].value,
          this.stockForm.controls['high'].value,
          this.stockForm.controls['low'].value,
          this.stockForm.controls['volume'].value);
        this.stockService.updateStock(this.id, stock).subscribe(result => {
          this.redirectStockPage();
        }, error => console.error(error))
      } else {
        let stock: Stock = new Stock(null,
          this.stockForm.controls['symbol'].value,
          new Date(this.stockForm.controls['date'].value),
          this.stockForm.controls['open'].value,
          this.stockForm.controls['close'].value,
          this.stockForm.controls['high'].value,
          this.stockForm.controls['low'].value,
          this.stockForm.controls['volume'].value);
        this.stockService.saveStock(stock).subscribe(result => {
          this.redirectStockPage();
        }, error => console.error(error))
      }
    }
  }


  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  clear() {
    console.log(this.stockForm.controls['date']);
      this.stockForm.patchValue({
        date: null,
      });
  }

  today() {
    console.log(this.stockForm.controls['date']);
    this.stockForm.patchValue({
      date: new DatePipe(navigator.language).transform(new Date(), 'yyyy-MM-ddTHH:mm' ),
    });
  }

  redirectStockPage() {
    this.router.navigate(['/stock']);
  }
}
