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
import { ICustomers } from 'app/entities/customers/customers.model';
import { CustomersService } from 'app/entities/customers/service/customers.service';

import { PaymentHistoryUpdateComponent } from './payment-history-update.component';

describe('PaymentHistory Management Update Component', () => {
  let comp: PaymentHistoryUpdateComponent;
  let fixture: ComponentFixture<PaymentHistoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let paymentHistoryFormService: PaymentHistoryFormService;
  let paymentHistoryService: PaymentHistoryService;
  let customersService: CustomersService;

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
    customersService = TestBed.inject(CustomersService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Customers query and add missing value', () => {
      const paymentHistory: IPaymentHistory = { id: 456 };
      const customer: ICustomers = { id: 13974 };
      paymentHistory.customer = customer;

      const customersCollection: ICustomers[] = [{ id: 90270 }];
      jest.spyOn(customersService, 'query').mockReturnValue(of(new HttpResponse({ body: customersCollection })));
      const additionalCustomers = [customer];
      const expectedCollection: ICustomers[] = [...additionalCustomers, ...customersCollection];
      jest.spyOn(customersService, 'addCustomersToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ paymentHistory });
      comp.ngOnInit();

      expect(customersService.query).toHaveBeenCalled();
      expect(customersService.addCustomersToCollectionIfMissing).toHaveBeenCalledWith(
        customersCollection,
        ...additionalCustomers.map(expect.objectContaining)
      );
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const paymentHistory: IPaymentHistory = { id: 456 };
      const customer: ICustomers = { id: 21822 };
      paymentHistory.customer = customer;

      activatedRoute.data = of({ paymentHistory });
      comp.ngOnInit();

      expect(comp.customersSharedCollection).toContain(customer);
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

  describe('Compare relationships', () => {
    describe('compareCustomers', () => {
      it('Should forward to customersService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(customersService, 'compareCustomers');
        comp.compareCustomers(entity, entity2);
        expect(customersService.compareCustomers).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
