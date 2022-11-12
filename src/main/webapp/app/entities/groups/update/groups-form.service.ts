import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IGroups, NewGroups } from '../groups.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGroups for edit and NewGroupsFormGroupInput for create.
 */
type GroupsFormGroupInput = IGroups | PartialWithRequiredKeyOf<NewGroups>;

type GroupsFormDefaults = Pick<NewGroups, 'id' | 'users'>;

type GroupsFormGroupContent = {
  id: FormControl<IGroups['id'] | NewGroups['id']>;
  name: FormControl<IGroups['name']>;
  grOwner: FormControl<IGroups['grOwner']>;
  organization: FormControl<IGroups['organization']>;
  users: FormControl<IGroups['users']>;
};

export type GroupsFormGroup = FormGroup<GroupsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class GroupsFormService {
  createGroupsFormGroup(groups: GroupsFormGroupInput = { id: null }): GroupsFormGroup {
    const groupsRawValue = {
      ...this.getFormDefaults(),
      ...groups,
    };
    return new FormGroup<GroupsFormGroupContent>({
      id: new FormControl(
        { value: groupsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(groupsRawValue.name, {
        validators: [Validators.required],
      }),
      grOwner: new FormControl(groupsRawValue.grOwner),
      organization: new FormControl(groupsRawValue.organization),
      users: new FormControl(groupsRawValue.users ?? []),
    });
  }

  getGroups(form: GroupsFormGroup): IGroups | NewGroups {
    return form.getRawValue() as IGroups | NewGroups;
  }

  resetForm(form: GroupsFormGroup, groups: GroupsFormGroupInput): void {
    const groupsRawValue = { ...this.getFormDefaults(), ...groups };
    form.reset(
      {
        ...groupsRawValue,
        id: { value: groupsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): GroupsFormDefaults {
    return {
      id: null,
      users: [],
    };
  }
}
