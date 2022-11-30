import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITelegramEntity, NewTelegramEntity } from '../telegram-entity.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITelegramEntity for edit and NewTelegramEntityFormGroupInput for create.
 */
type TelegramEntityFormGroupInput = ITelegramEntity | PartialWithRequiredKeyOf<NewTelegramEntity>;

type TelegramEntityFormDefaults = Pick<NewTelegramEntity, 'id' | 'isBot' | 'canJoinGroups' | 'isActive'>;

type TelegramEntityFormGroupContent = {
  id: FormControl<ITelegramEntity['id'] | NewTelegramEntity['id']>;
  isBot: FormControl<ITelegramEntity['isBot']>;
  firstname: FormControl<ITelegramEntity['firstname']>;
  lastname: FormControl<ITelegramEntity['lastname']>;
  username: FormControl<ITelegramEntity['username']>;
  telegramId: FormControl<ITelegramEntity['telegramId']>;
  canJoinGroups: FormControl<ITelegramEntity['canJoinGroups']>;
  languageCode: FormControl<ITelegramEntity['languageCode']>;
  isActive: FormControl<ITelegramEntity['isActive']>;
  user: FormControl<ITelegramEntity['user']>;
};

export type TelegramEntityFormGroup = FormGroup<TelegramEntityFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TelegramEntityFormService {
  createTelegramEntityFormGroup(telegramEntity: TelegramEntityFormGroupInput = { id: null }): TelegramEntityFormGroup {
    const telegramEntityRawValue = {
      ...this.getFormDefaults(),
      ...telegramEntity,
    };
    return new FormGroup<TelegramEntityFormGroupContent>({
      id: new FormControl(
        { value: telegramEntityRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      isBot: new FormControl(telegramEntityRawValue.isBot),
      firstname: new FormControl(telegramEntityRawValue.firstname),
      lastname: new FormControl(telegramEntityRawValue.lastname),
      username: new FormControl(telegramEntityRawValue.username),
      telegramId: new FormControl(telegramEntityRawValue.telegramId),
      canJoinGroups: new FormControl(telegramEntityRawValue.canJoinGroups),
      languageCode: new FormControl(telegramEntityRawValue.languageCode),
      isActive: new FormControl(telegramEntityRawValue.isActive),
      user: new FormControl(telegramEntityRawValue.user),
    });
  }

  getTelegramEntity(form: TelegramEntityFormGroup): ITelegramEntity | NewTelegramEntity {
    return form.getRawValue() as ITelegramEntity | NewTelegramEntity;
  }

  resetForm(form: TelegramEntityFormGroup, telegramEntity: TelegramEntityFormGroupInput): void {
    const telegramEntityRawValue = { ...this.getFormDefaults(), ...telegramEntity };
    form.reset(
      {
        ...telegramEntityRawValue,
        id: { value: telegramEntityRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TelegramEntityFormDefaults {
    return {
      id: null,
      isBot: false,
      canJoinGroups: false,
      isActive: false,
    };
  }
}
