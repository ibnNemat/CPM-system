import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../customers.test-samples';

import { CustomersFormService } from './customers-form.service';

describe('Customers Form Service', () => {
  let service: CustomersFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CustomersFormService);
  });

  describe('Service methods', () => {
    describe('createCustomersFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCustomersFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            username: expect.any(Object),
            password: expect.any(Object),
            phoneNumber: expect.any(Object),
            account: expect.any(Object),
            user: expect.any(Object),
            groups: expect.any(Object),
          })
        );
      });

      it('passing ICustomers should create a new form with FormGroup', () => {
        const formGroup = service.createCustomersFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            username: expect.any(Object),
            password: expect.any(Object),
            phoneNumber: expect.any(Object),
            account: expect.any(Object),
            user: expect.any(Object),
            groups: expect.any(Object),
          })
        );
      });
    });

    describe('getCustomers', () => {
      it('should return NewCustomers for default Customers initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createCustomersFormGroup(sampleWithNewData);

        const customers = service.getCustomers(formGroup) as any;

        expect(customers).toMatchObject(sampleWithNewData);
      });

      it('should return NewCustomers for empty Customers initial value', () => {
        const formGroup = service.createCustomersFormGroup();

        const customers = service.getCustomers(formGroup) as any;

        expect(customers).toMatchObject({});
      });

      it('should return ICustomers', () => {
        const formGroup = service.createCustomersFormGroup(sampleWithRequiredData);

        const customers = service.getCustomers(formGroup) as any;

        expect(customers).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICustomers should not enable id FormControl', () => {
        const formGroup = service.createCustomersFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCustomers should disable id FormControl', () => {
        const formGroup = service.createCustomersFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
