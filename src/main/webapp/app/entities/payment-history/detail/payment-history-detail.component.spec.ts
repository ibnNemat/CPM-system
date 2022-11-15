import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PaymentHistoryDetailComponent } from './payment-history-detail.component';

describe('PaymentHistory Management Detail Component', () => {
  let comp: PaymentHistoryDetailComponent;
  let fixture: ComponentFixture<PaymentHistoryDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PaymentHistoryDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ paymentHistory: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PaymentHistoryDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PaymentHistoryDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load paymentHistory on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.paymentHistory).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
