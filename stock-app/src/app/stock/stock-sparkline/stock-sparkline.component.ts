import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
declare let d3: any;

// @Component({
//   selector: 'app-stock-sparkline',
//   templateUrl: './stock-sparkline.component.html',
//   styleUrls: ['./stock-sparkline.component.css']
// })

@Component({
    selector: 'sparkline',
    templateUrl: './stock-sparkline.component.html',
    // include original styles
    styleUrls: [
        './stock-sparkline.component.css'
    ],
    encapsulation: ViewEncapsulation.None
})



export class StockSparklineComponent implements OnInit {

options;
data;

@Input() dataseries;
@Input() datafield: string;

ngOnInit() {

    this.data = sparklineChart(this.dataseries, this.datafield); //volatileChart(25.0, 0.09,30);

    this.options = {
        chart: {
            type: 'sparklinePlus',
            height: 40,
            x: function(d, i){return i;},
            xTickFormat: function(d) {
                return d
            },
            duration: 150
        }
    }

    function sparklineChart(dataseries, datafield) {
        var rval = [];
        for (var i = 1; i < dataseries.length; i++) {
            if(datafield == 'volume') {
                rval.push({x: dataseries[i].time_part, y: dataseries[i].volume});
            } else {
                rval.push({x: dataseries[i].time_part, y: dataseries[i].pRange});
            }
        }
        return rval;
    }
}
}
