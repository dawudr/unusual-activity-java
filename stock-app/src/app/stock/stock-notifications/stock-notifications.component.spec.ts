import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StockNotificationsComponent } from './stock-notifications.component';

describe('StockNotificationsComponent', () => {
  let component: StockNotificationsComponent;
  let fixture: ComponentFixture<StockNotificationsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StockNotificationsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StockNotificationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
