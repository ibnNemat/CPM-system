import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITelegramGroup, NewTelegramGroup } from '../telegram-group.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITelegramGroup for edit and NewTelegramGroupFormGroupInput for create.
 */
type TelegramGroupFormGroupInput = ITelegramGroup | PartialWithRequiredKeyOf<NewTelegramGroup>;

type TelegramGroupFormDefaults = Pick<NewTelegramGroup, 'id' | 'customerTelegrams'>;

type TelegramGroupFormGroupContent = {
  id: FormControl<ITelegramGroup['id'] | NewTelegramGroup['id']>;
  name: FormControl<ITelegramGroup['name']>;
  chatId: FormControl<ITelegramGroup['chatId']>;
  customerTelegrams: FormControl<ITelegramGroup['customerTelegrams']>;
};

export type TelegramGroupFormGroup = FormGroup<TelegramGroupFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TelegramGroupFormService {
  createTelegramGroupFormGroup(telegramGroup: TelegramGroupFormGroupInput = { id: null }): TelegramGroupFormGroup {
    const telegramGroupRawValue = {
      ...this.getFormDefaults(),
      ...telegramGroup,
    };
    return new FormGroup<TelegramGroupFormGroupContent>({
      id: new FormControl(
        { value: telegramGroupRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(telegramGroupRawValue.name),
      chatId: new FormControl(telegramGroupRawValue.chatId),
      customerTelegrams: new FormControl(telegramGroupRawValue.customerTelegrams ?? []),
    });
  }

  getTelegramGroup(form: TelegramGroupFormGroup): ITelegramGroup | NewTelegramGroup {
    return form.getRawValue() as ITelegramGroup | NewTelegramGroup;
  }

  resetForm(form: TelegramGroupFormGroup, telegramGroup: TelegramGroupFormGroupInput): void {
    const telegramGroupRawValue = { ...this.getFormDefaults(), ...telegramGroup };
    form.reset(
      {
        ...telegramGroupRawValue,
        id: { value: telegramGroupRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TelegramGroupFormDefaults {
    return {
      id: null,
      customerTelegrams: [],
    };
  }
}
