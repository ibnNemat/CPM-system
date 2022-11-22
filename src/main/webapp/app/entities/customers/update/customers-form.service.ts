import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICustomers, NewCustomers } from '../customers.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICustomers for edit and NewCustomersFormGroupInput for create.
 */
type CustomersFormGroupInput = ICustomers | PartialWithRequiredKeyOf<NewCustomers>;

type CustomersFormDefaults = Pick<NewCustomers, 'id' | 'groups'>;

type CustomersFormGroupContent = {
  id: FormControl<ICustomers['id'] | NewCustomers['id']>;
  username: FormControl<ICustomers['username']>;
  password: FormControl<ICustomers['password']>;
  phoneNumber: FormControl<ICustomers['phoneNumber']>;
  account: FormControl<ICustomers['account']>;
  user: FormControl<ICustomers['user']>;
  groups: FormControl<ICustomers['groups']>;
};

export type CustomersFormGroup = FormGroup<CustomersFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CustomersFormService {
  createCustomersFormGroup(customers: CustomersFormGroupInput = { id: null }): CustomersFormGroup {
    const customersRawValue = {
      ...this.getFormDefaults(),
      ...customers,
    };
    return new FormGroup<CustomersFormGroupContent>({
      id: new FormControl(
        { value: customersRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      username: new FormControl(customersRawValue.username, {
        validators: [Validators.required],
      }),
      password: new FormControl(customersRawValue.password, {
        validators: [Validators.required],
      }),
      phoneNumber: new FormControl(customersRawValue.phoneNumber, {
        validators: [Validators.required],
      }),
      account: new FormControl(customersRawValue.account, {
        validators: [Validators.required, Validators.min(0)],
      }),
      user: new FormControl(customersRawValue.user),
      groups: new FormControl(customersRawValue.groups ?? []),
    });
  }

  getCustomers(form: CustomersFormGroup): ICustomers | NewCustomers {
    return form.getRawValue() as ICustomers | NewCustomers;
  }

  resetForm(form: CustomersFormGroup, customers: CustomersFormGroupInput): void {
    const customersRawValue = { ...this.getFormDefaults(), ...customers };
    form.reset(
      {
        ...customersRawValue,
        id: { value: customersRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CustomersFormDefaults {
    return {
      id: null,
      groups: [],
    };
  }
}
