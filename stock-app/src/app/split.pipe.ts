import {PipeTransform, Pipe} from '@angular/core';

@Pipe({ name: 'split' })
export class SplitPipe implements PipeTransform {
    transform(value:string, [separator]):string {
        let splits = value.split(separator);
        if(splits.length > 1) {
            return splits.pop();
        } else {
            return '';
        }
    }
}