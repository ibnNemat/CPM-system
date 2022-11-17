import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPaymentHistory, NewPaymentHistory } from '../payment-history.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPaymentHistory for edit and NewPaymentHistoryFormGroupInput for create.
 */
type PaymentHistoryFormGroupInput = IPaymentHistory | PartialWithRequiredKeyOf<NewPaymentHistory>;

type PaymentHistoryFormDefaults = Pick<NewPaymentHistory, 'id'>;

type PaymentHistoryFormGroupContent = {
  id: FormControl<IPaymentHistory['id'] | NewPaymentHistory['id']>;
  organizationName: FormControl<IPaymentHistory['organizationName']>;
  groupName: FormControl<IPaymentHistory['groupName']>;
  serviceName: FormControl<IPaymentHistory['serviceName']>;
  sum: FormControl<IPaymentHistory['sum']>;
  createdAt: FormControl<IPaymentHistory['createdAt']>;
  customer: FormControl<IPaymentHistory['customer']>;
};

export type PaymentHistoryFormGroup = FormGroup<PaymentHistoryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PaymentHistoryFormService {
  createPaymentHistoryFormGroup(paymentHistory: PaymentHistoryFormGroupInput = { id: null }): PaymentHistoryFormGroup {
    const paymentHistoryRawValue = {
      ...this.getFormDefaults(),
      ...paymentHistory,
    };
    return new FormGroup<PaymentHistoryFormGroupContent>({
      id: new FormControl(
        { value: paymentHistoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      organizationName: new FormControl(paymentHistoryRawValue.organizationName),
      groupName: new FormControl(paymentHistoryRawValue.groupName),
      serviceName: new FormControl(paymentHistoryRawValue.serviceName),
      sum: new FormControl(paymentHistoryRawValue.sum),
      createdAt: new FormControl(paymentHistoryRawValue.createdAt),
      customer: new FormControl(paymentHistoryRawValue.customer),
    });
  }

  getPaymentHistory(form: PaymentHistoryFormGroup): IPaymentHistory | NewPaymentHistory {
    return form.getRawValue() as IPaymentHistory | NewPaymentHistory;
  }

  resetForm(form: PaymentHistoryFormGroup, paymentHistory: PaymentHistoryFormGroupInput): void {
    const paymentHistoryRawValue = { ...this.getFormDefaults(), ...paymentHistory };
    form.reset(
      {
        ...paymentHistoryRawValue,
        id: { value: paymentHistoryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PaymentHistoryFormDefaults {
    return {
      id: null,
    };
  }
}
