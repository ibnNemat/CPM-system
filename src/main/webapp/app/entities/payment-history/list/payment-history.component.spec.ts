import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { PaymentHistoryService } from '../service/payment-history.service';

import { PaymentHistoryComponent } from './payment-history.component';

describe('PaymentHistory Management Component', () => {
  let comp: PaymentHistoryComponent;
  let fixture: ComponentFixture<PaymentHistoryComponent>;
  let service: PaymentHistoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'payment-history', component: PaymentHistoryComponent }]), HttpClientTestingModule],
      declarations: [PaymentHistoryComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(PaymentHistoryComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PaymentHistoryComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PaymentHistoryService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.paymentHistories?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to paymentHistoryService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getPaymentHistoryIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getPaymentHistoryIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
