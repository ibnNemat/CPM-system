import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../bot-token.test-samples';

import { BotTokenFormService } from './bot-token-form.service';

describe('BotToken Form Service', () => {
  let service: BotTokenFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BotTokenFormService);
  });

  describe('Service methods', () => {
    describe('createBotTokenFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBotTokenFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            username: expect.any(Object),
            telegramId: expect.any(Object),
            token: expect.any(Object),
            createdBy: expect.any(Object),
          })
        );
      });

      it('passing IBotToken should create a new form with FormGroup', () => {
        const formGroup = service.createBotTokenFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            username: expect.any(Object),
            telegramId: expect.any(Object),
            token: expect.any(Object),
            createdBy: expect.any(Object),
          })
        );
      });
    });

    describe('getBotToken', () => {
      it('should return NewBotToken for default BotToken initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createBotTokenFormGroup(sampleWithNewData);

        const botToken = service.getBotToken(formGroup) as any;

        expect(botToken).toMatchObject(sampleWithNewData);
      });

      it('should return NewBotToken for empty BotToken initial value', () => {
        const formGroup = service.createBotTokenFormGroup();

        const botToken = service.getBotToken(formGroup) as any;

        expect(botToken).toMatchObject({});
      });

      it('should return IBotToken', () => {
        const formGroup = service.createBotTokenFormGroup(sampleWithRequiredData);

        const botToken = service.getBotToken(formGroup) as any;

        expect(botToken).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBotToken should not enable id FormControl', () => {
        const formGroup = service.createBotTokenFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBotToken should disable id FormControl', () => {
        const formGroup = service.createBotTokenFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
