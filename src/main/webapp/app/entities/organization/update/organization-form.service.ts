import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IOrganization, NewOrganization } from '../organization.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOrganization for edit and NewOrganizationFormGroupInput for create.
 */
type OrganizationFormGroupInput = IOrganization | PartialWithRequiredKeyOf<NewOrganization>;

type OrganizationFormDefaults = Pick<NewOrganization, 'id'>;

type OrganizationFormGroupContent = {
  id: FormControl<IOrganization['id'] | NewOrganization['id']>;
  name: FormControl<IOrganization['name']>;
};

export type OrganizationFormGroup = FormGroup<OrganizationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrganizationFormService {
  createOrganizationFormGroup(organization: OrganizationFormGroupInput = { id: null }): OrganizationFormGroup {
    const organizationRawValue = {
      ...this.getFormDefaults(),
      ...organization,
    };
    return new FormGroup<OrganizationFormGroupContent>({
      id: new FormControl(
        { value: organizationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(organizationRawValue.name, {
        validators: [Validators.required],
      }),
    });
  }

  getOrganization(form: OrganizationFormGroup): IOrganization | NewOrganization {
    return form.getRawValue() as IOrganization | NewOrganization;
  }

  resetForm(form: OrganizationFormGroup, organization: OrganizationFormGroupInput): void {
    const organizationRawValue = { ...this.getFormDefaults(), ...organization };
    form.reset(
      {
        ...organizationRawValue,
        id: { value: organizationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OrganizationFormDefaults {
    return {
      id: null,
    };
  }
}
