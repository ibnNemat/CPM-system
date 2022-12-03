import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../telegram-group.test-samples';

import { TelegramGroupFormService } from './telegram-group-form.service';

describe('TelegramGroup Form Service', () => {
  let service: TelegramGroupFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TelegramGroupFormService);
  });

  describe('Service methods', () => {
    describe('createTelegramGroupFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTelegramGroupFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            chatId: expect.any(Object),
            customerTelegrams: expect.any(Object),
          })
        );
      });

      it('passing ITelegramGroup should create a new form with FormGroup', () => {
        const formGroup = service.createTelegramGroupFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            chatId: expect.any(Object),
            customerTelegrams: expect.any(Object),
          })
        );
      });
    });

    describe('getTelegramGroup', () => {
      it('should return NewTelegramGroup for default TelegramGroup initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTelegramGroupFormGroup(sampleWithNewData);

        const telegramGroup = service.getTelegramGroup(formGroup) as any;

        expect(telegramGroup).toMatchObject(sampleWithNewData);
      });

      it('should return NewTelegramGroup for empty TelegramGroup initial value', () => {
        const formGroup = service.createTelegramGroupFormGroup();

        const telegramGroup = service.getTelegramGroup(formGroup) as any;

        expect(telegramGroup).toMatchObject({});
      });

      it('should return ITelegramGroup', () => {
        const formGroup = service.createTelegramGroupFormGroup(sampleWithRequiredData);

        const telegramGroup = service.getTelegramGroup(formGroup) as any;

        expect(telegramGroup).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITelegramGroup should not enable id FormControl', () => {
        const formGroup = service.createTelegramGroupFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTelegramGroup should disable id FormControl', () => {
        const formGroup = service.createTelegramGroupFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
