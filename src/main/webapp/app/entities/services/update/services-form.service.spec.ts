import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../services.test-samples';

import { ServicesFormService } from './services-form.service';

describe('Services Form Service', () => {
  let service: ServicesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServicesFormService);
  });

  describe('Service methods', () => {
    describe('createServicesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createServicesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            price: expect.any(Object),
            startedPeriod: expect.any(Object),
            periodType: expect.any(Object),
            countPeriod: expect.any(Object),
            groups: expect.any(Object),
          })
        );
      });

      it('passing IServices should create a new form with FormGroup', () => {
        const formGroup = service.createServicesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            price: expect.any(Object),
            startedPeriod: expect.any(Object),
            periodType: expect.any(Object),
            countPeriod: expect.any(Object),
            groups: expect.any(Object),
          })
        );
      });
    });

    describe('getServices', () => {
      it('should return NewServices for default Services initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createServicesFormGroup(sampleWithNewData);

        const services = service.getServices(formGroup) as any;

        expect(services).toMatchObject(sampleWithNewData);
      });

      it('should return NewServices for empty Services initial value', () => {
        const formGroup = service.createServicesFormGroup();

        const services = service.getServices(formGroup) as any;

        expect(services).toMatchObject({});
      });

      it('should return IServices', () => {
        const formGroup = service.createServicesFormGroup(sampleWithRequiredData);

        const services = service.getServices(formGroup) as any;

        expect(services).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IServices should not enable id FormControl', () => {
        const formGroup = service.createServicesFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewServices should disable id FormControl', () => {
        const formGroup = service.createServicesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
