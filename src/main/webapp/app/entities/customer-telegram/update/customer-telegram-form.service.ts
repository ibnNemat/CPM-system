import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICustomerTelegram, NewCustomerTelegram } from '../customer-telegram.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICustomerTelegram for edit and NewCustomerTelegramFormGroupInput for create.
 */
type CustomerTelegramFormGroupInput = ICustomerTelegram | PartialWithRequiredKeyOf<NewCustomerTelegram>;

type CustomerTelegramFormDefaults = Pick<NewCustomerTelegram, 'id' | 'isBot' | 'canJoinGroups' | 'isActive' | 'telegramGroups'>;

type CustomerTelegramFormGroupContent = {
  id: FormControl<ICustomerTelegram['id'] | NewCustomerTelegram['id']>;
  isBot: FormControl<ICustomerTelegram['isBot']>;
  firstname: FormControl<ICustomerTelegram['firstname']>;
  lastname: FormControl<ICustomerTelegram['lastname']>;
  username: FormControl<ICustomerTelegram['username']>;
  telegramId: FormControl<ICustomerTelegram['telegramId']>;
  phoneNumber: FormControl<ICustomerTelegram['phoneNumber']>;
  step: FormControl<ICustomerTelegram['step']>;
  canJoinGroups: FormControl<ICustomerTelegram['canJoinGroups']>;
  languageCode: FormControl<ICustomerTelegram['languageCode']>;
  isActive: FormControl<ICustomerTelegram['isActive']>;
  customer: FormControl<ICustomerTelegram['customer']>;
  telegramGroups: FormControl<ICustomerTelegram['telegramGroups']>;
};

export type CustomerTelegramFormGroup = FormGroup<CustomerTelegramFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CustomerTelegramFormService {
  createCustomerTelegramFormGroup(customerTelegram: CustomerTelegramFormGroupInput = { id: null }): CustomerTelegramFormGroup {
    const customerTelegramRawValue = {
      ...this.getFormDefaults(),
      ...customerTelegram,
    };
    return new FormGroup<CustomerTelegramFormGroupContent>({
      id: new FormControl(
        { value: customerTelegramRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      isBot: new FormControl(customerTelegramRawValue.isBot),
      firstname: new FormControl(customerTelegramRawValue.firstname),
      lastname: new FormControl(customerTelegramRawValue.lastname),
      username: new FormControl(customerTelegramRawValue.username),
      telegramId: new FormControl(customerTelegramRawValue.telegramId),
      phoneNumber: new FormControl(customerTelegramRawValue.phoneNumber),
      step: new FormControl(customerTelegramRawValue.step),
      canJoinGroups: new FormControl(customerTelegramRawValue.canJoinGroups),
      languageCode: new FormControl(customerTelegramRawValue.languageCode),
      isActive: new FormControl(customerTelegramRawValue.isActive),
      customer: new FormControl(customerTelegramRawValue.customer),
      telegramGroups: new FormControl(customerTelegramRawValue.telegramGroups ?? []),
    });
  }

  getCustomerTelegram(form: CustomerTelegramFormGroup): ICustomerTelegram | NewCustomerTelegram {
    return form.getRawValue() as ICustomerTelegram | NewCustomerTelegram;
  }

  resetForm(form: CustomerTelegramFormGroup, customerTelegram: CustomerTelegramFormGroupInput): void {
    const customerTelegramRawValue = { ...this.getFormDefaults(), ...customerTelegram };
    form.reset(
      {
        ...customerTelegramRawValue,
        id: { value: customerTelegramRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CustomerTelegramFormDefaults {
    return {
      id: null,
      isBot: false,
      canJoinGroups: false,
      isActive: false,
      telegramGroups: [],
    };
  }
}
