import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import 'd3';
import 'nvd3';
import { ChartService } from "../chart.service";
import {ActivatedRoute, Router} from "@angular/router";
declare let d3: any;

@Component({
  selector: 'app-stock-chart',
  templateUrl: './stock-chart.component.html',
  // include original styles
  styleUrls: [
    './stock-chart.component.css',
    './../../../../node_modules/nvd3/build/nv.d3.css'
  ],
  providers: [ChartService],
  encapsulation: ViewEncapsulation.None
})

export class StockChartComponent implements OnInit {

  constructor(private route: ActivatedRoute,
              private router: Router,
              private chartService: ChartService) { }

  symbol: string;
  sector: string;
  options;
  data;

  private sub: any;

  ngOnInit() {

    this.sub = this.route.params.subscribe(params => {
      this.symbol = params['symbol'];
      console.log("Request param symbol:" + this.symbol);

      this.sector = params['sector'];
      console.log("Request param sector:" + this.sector);
    });

    // if(this.symbol) {
    //   // this.data = this.chartService.getChartBoxPlotBySymbol(this.symbol);
    // } else {
      this.data = this.getBoxPlotChartSeries();
    // }


    this.options = {
      chart: {
        type: 'boxPlotChart',
        height: 600,
        margin : {
          top: 20,
          right: 20,
          bottom: 30,
          left: 50
        },
        color:['darkblue', 'darkorange', 'green', 'darkred', 'darkviolet'],
        x: function(d){return d.label;},
        //y: function(d){return d.values.Q3;},
        maxBoxWidth: 55,
        // yDomain: [0, 500]
      }
    }
    // this.data = [
    //   {
    //     label: "Sample A",
    //     values: {
    //       "Q1": 180,
    //       "Q2": 200,
    //       "Q3": 290,
    //       whisker_low: 115,
    //       whisker_high: 400,
    //       outliers: [50, 100, 425]
    //     }
    //   },
    //   {
    //     label: "Sample B",
    //     values: {
    //       Q1: 300,
    //       Q2: 350,
    //       Q3: 400,
    //       whisker_low: 225,
    //       whisker_high: 425,
    //       outliers: [175, 450, 480]
    //     }
    //   },
    //   {
    //     label: "Sample C",
    //     values: {
    //       Q1: 100,
    //       Q2: 200,
    //       Q3: 300,
    //       whisker_low: 25,
    //       whisker_high: 400,
    //       outliers: [450, 475]
    //     }
    //   }
    // ];
  }

  getBoxPlotChartSeries() {
    this.chartService.getBoxPlot().subscribe(data => {
        return this.data = data;
      },
      err => {
        console.log(err);
      }
    );
  }

}
