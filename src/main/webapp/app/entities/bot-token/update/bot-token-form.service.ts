import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IBotToken, NewBotToken } from '../bot-token.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBotToken for edit and NewBotTokenFormGroupInput for create.
 */
type BotTokenFormGroupInput = IBotToken | PartialWithRequiredKeyOf<NewBotToken>;

type BotTokenFormDefaults = Pick<NewBotToken, 'id'>;

type BotTokenFormGroupContent = {
  id: FormControl<IBotToken['id'] | NewBotToken['id']>;
  username: FormControl<IBotToken['username']>;
  telegramId: FormControl<IBotToken['telegramId']>;
  token: FormControl<IBotToken['token']>;
  createdBy: FormControl<IBotToken['createdBy']>;
};

export type BotTokenFormGroup = FormGroup<BotTokenFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BotTokenFormService {
  createBotTokenFormGroup(botToken: BotTokenFormGroupInput = { id: null }): BotTokenFormGroup {
    const botTokenRawValue = {
      ...this.getFormDefaults(),
      ...botToken,
    };
    return new FormGroup<BotTokenFormGroupContent>({
      id: new FormControl(
        { value: botTokenRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      username: new FormControl(botTokenRawValue.username, {
        validators: [Validators.required],
      }),
      telegramId: new FormControl(botTokenRawValue.telegramId, {
        validators: [Validators.required],
      }),
      token: new FormControl(botTokenRawValue.token, {
        validators: [Validators.required],
      }),
      createdBy: new FormControl(botTokenRawValue.createdBy),
    });
  }

  getBotToken(form: BotTokenFormGroup): IBotToken | NewBotToken {
    return form.getRawValue() as IBotToken | NewBotToken;
  }

  resetForm(form: BotTokenFormGroup, botToken: BotTokenFormGroupInput): void {
    const botTokenRawValue = { ...this.getFormDefaults(), ...botToken };
    form.reset(
      {
        ...botTokenRawValue,
        id: { value: botTokenRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): BotTokenFormDefaults {
    return {
      id: null,
    };
  }
}
