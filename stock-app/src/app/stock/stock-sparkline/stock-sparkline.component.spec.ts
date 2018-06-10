import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StockSparklineComponent } from './stock-sparkline.component';

describe('StockSparklineComponent', () => {
  let component: StockSparklineComponent;
  let fixture: ComponentFixture<StockSparklineComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StockSparklineComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StockSparklineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
