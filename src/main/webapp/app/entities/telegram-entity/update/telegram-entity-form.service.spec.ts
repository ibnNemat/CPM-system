import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../telegram-entity.test-samples';

import { TelegramEntityFormService } from './telegram-entity-form.service';

describe('TelegramEntity Form Service', () => {
  let service: TelegramEntityFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TelegramEntityFormService);
  });

  describe('Service methods', () => {
    describe('createTelegramEntityFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTelegramEntityFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            isBot: expect.any(Object),
            firstname: expect.any(Object),
            lastname: expect.any(Object),
            username: expect.any(Object),
            telegramId: expect.any(Object),
            canJoinGroups: expect.any(Object),
            languageCode: expect.any(Object),
            isActive: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });

      it('passing ITelegramEntity should create a new form with FormGroup', () => {
        const formGroup = service.createTelegramEntityFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            isBot: expect.any(Object),
            firstname: expect.any(Object),
            lastname: expect.any(Object),
            username: expect.any(Object),
            telegramId: expect.any(Object),
            canJoinGroups: expect.any(Object),
            languageCode: expect.any(Object),
            isActive: expect.any(Object),
            user: expect.any(Object),
          })
        );
      });
    });

    describe('getTelegramEntity', () => {
      it('should return NewTelegramEntity for default TelegramEntity initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createTelegramEntityFormGroup(sampleWithNewData);

        const telegramEntity = service.getTelegramEntity(formGroup) as any;

        expect(telegramEntity).toMatchObject(sampleWithNewData);
      });

      it('should return NewTelegramEntity for empty TelegramEntity initial value', () => {
        const formGroup = service.createTelegramEntityFormGroup();

        const telegramEntity = service.getTelegramEntity(formGroup) as any;

        expect(telegramEntity).toMatchObject({});
      });

      it('should return ITelegramEntity', () => {
        const formGroup = service.createTelegramEntityFormGroup(sampleWithRequiredData);

        const telegramEntity = service.getTelegramEntity(formGroup) as any;

        expect(telegramEntity).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITelegramEntity should not enable id FormControl', () => {
        const formGroup = service.createTelegramEntityFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTelegramEntity should disable id FormControl', () => {
        const formGroup = service.createTelegramEntityFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
