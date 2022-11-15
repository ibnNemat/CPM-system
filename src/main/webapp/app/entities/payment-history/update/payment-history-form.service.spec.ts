import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../payment-history.test-samples';

import { PaymentHistoryFormService } from './payment-history-form.service';

describe('PaymentHistory Form Service', () => {
  let service: PaymentHistoryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PaymentHistoryFormService);
  });

  describe('Service methods', () => {
    describe('createPaymentHistoryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPaymentHistoryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            organizationName: expect.any(Object),
            serviceName: expect.any(Object),
            groupName: expect.any(Object),
            sum: expect.any(Object),
            createdAt: expect.any(Object),
          })
        );
      });

      it('passing IPaymentHistory should create a new form with FormGroup', () => {
        const formGroup = service.createPaymentHistoryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            organizationName: expect.any(Object),
            serviceName: expect.any(Object),
            groupName: expect.any(Object),
            sum: expect.any(Object),
            createdAt: expect.any(Object),
          })
        );
      });
    });

    describe('getPaymentHistory', () => {
      it('should return NewPaymentHistory for default PaymentHistory initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createPaymentHistoryFormGroup(sampleWithNewData);

        const paymentHistory = service.getPaymentHistory(formGroup) as any;

        expect(paymentHistory).toMatchObject(sampleWithNewData);
      });

      it('should return NewPaymentHistory for empty PaymentHistory initial value', () => {
        const formGroup = service.createPaymentHistoryFormGroup();

        const paymentHistory = service.getPaymentHistory(formGroup) as any;

        expect(paymentHistory).toMatchObject({});
      });

      it('should return IPaymentHistory', () => {
        const formGroup = service.createPaymentHistoryFormGroup(sampleWithRequiredData);

        const paymentHistory = service.getPaymentHistory(formGroup) as any;

        expect(paymentHistory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPaymentHistory should not enable id FormControl', () => {
        const formGroup = service.createPaymentHistoryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPaymentHistory should disable id FormControl', () => {
        const formGroup = service.createPaymentHistoryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
