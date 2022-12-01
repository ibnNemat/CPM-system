import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../customer-telegram.test-samples';

import { CustomerTelegramFormService } from './customer-telegram-form.service';

describe('CustomerTelegram Form Service', () => {
  let service: CustomerTelegramFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CustomerTelegramFormService);
  });

  describe('Service methods', () => {
    describe('createCustomerTelegramFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCustomerTelegramFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            isBot: expect.any(Object),
            firstname: expect.any(Object),
            lastname: expect.any(Object),
            username: expect.any(Object),
            telegramId: expect.any(Object),
            phoneNumber: expect.any(Object),
            step: expect.any(Object),
            canJoinGroups: expect.any(Object),
            languageCode: expect.any(Object),
            isActive: expect.any(Object),
            customer: expect.any(Object),
          })
        );
      });

      it('passing ICustomerTelegram should create a new form with FormGroup', () => {
        const formGroup = service.createCustomerTelegramFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            isBot: expect.any(Object),
            firstname: expect.any(Object),
            lastname: expect.any(Object),
            username: expect.any(Object),
            telegramId: expect.any(Object),
            phoneNumber: expect.any(Object),
            step: expect.any(Object),
            canJoinGroups: expect.any(Object),
            languageCode: expect.any(Object),
            isActive: expect.any(Object),
            customer: expect.any(Object),
          })
        );
      });
    });

    describe('getCustomerTelegram', () => {
      it('should return NewCustomerTelegram for default CustomerTelegram initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createCustomerTelegramFormGroup(sampleWithNewData);

        const customerTelegram = service.getCustomerTelegram(formGroup) as any;

        expect(customerTelegram).toMatchObject(sampleWithNewData);
      });

      it('should return NewCustomerTelegram for empty CustomerTelegram initial value', () => {
        const formGroup = service.createCustomerTelegramFormGroup();

        const customerTelegram = service.getCustomerTelegram(formGroup) as any;

        expect(customerTelegram).toMatchObject({});
      });

      it('should return ICustomerTelegram', () => {
        const formGroup = service.createCustomerTelegramFormGroup(sampleWithRequiredData);

        const customerTelegram = service.getCustomerTelegram(formGroup) as any;

        expect(customerTelegram).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICustomerTelegram should not enable id FormControl', () => {
        const formGroup = service.createCustomerTelegramFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCustomerTelegram should disable id FormControl', () => {
        const formGroup = service.createCustomerTelegramFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
