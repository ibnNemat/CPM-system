import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PaymentHistoryFormService } from './payment-history-form.service';
import { PaymentHistoryService } from '../service/payment-history.service';
import { IPaymentHistory } from '../payment-history.model';

import { PaymentHistoryUpdateComponent } from './payment-history-update.component';

describe('PaymentHistory Management Update Component', () => {
  let comp: PaymentHistoryUpdateComponent;
  let fixture: ComponentFixture<PaymentHistoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let paymentHistoryFormService: PaymentHistoryFormService;
  let paymentHistoryService: PaymentHistoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PaymentHistoryUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PaymentHistoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PaymentHistoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    paymentHistoryFormService = TestBed.inject(PaymentHistoryFormService);
    paymentHistoryService = TestBed.inject(PaymentHistoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const paymentHistory: IPaymentHistory = { id: 456 };

      activatedRoute.data = of({ paymentHistory });
      comp.ngOnInit();

      expect(comp.paymentHistory).toEqual(paymentHistory);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPaymentHistory>>();
      const paymentHistory = { id: 123 };
      jest.spyOn(paymentHistoryFormService, 'getPaymentHistory').mockReturnValue(paymentHistory);
      jest.spyOn(paymentHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paymentHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: paymentHistory }));
      saveSubject.complete();

      // THEN
      expect(paymentHistoryFormService.getPaymentHistory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(paymentHistoryService.update).toHaveBeenCalledWith(expect.objectContaining(paymentHistory));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPaymentHistory>>();
      const paymentHistory = { id: 123 };
      jest.spyOn(paymentHistoryFormService, 'getPaymentHistory').mockReturnValue({ id: null });
      jest.spyOn(paymentHistoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paymentHistory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: paymentHistory }));
      saveSubject.complete();

      // THEN
      expect(paymentHistoryFormService.getPaymentHistory).toHaveBeenCalled();
      expect(paymentHistoryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPaymentHistory>>();
      const paymentHistory = { id: 123 };
      jest.spyOn(paymentHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paymentHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(paymentHistoryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
