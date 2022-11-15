import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../groups.test-samples';

import { GroupsFormService } from './groups-form.service';

describe('Groups Form Service', () => {
  let service: GroupsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GroupsFormService);
  });

  describe('Service methods', () => {
    describe('createGroupsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createGroupsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            groupManagerId: expect.any(Object),
            name: expect.any(Object),
            groupOwnerName: expect.any(Object),
            services: expect.any(Object),
            organization: expect.any(Object),
            users: expect.any(Object),
          })
        );
      });

      it('passing IGroups should create a new form with FormGroup', () => {
        const formGroup = service.createGroupsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            groupManagerId: expect.any(Object),
            name: expect.any(Object),
            groupOwnerName: expect.any(Object),
            services: expect.any(Object),
            organization: expect.any(Object),
            users: expect.any(Object),
          })
        );
      });
    });

    describe('getGroups', () => {
      it('should return NewGroups for default Groups initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createGroupsFormGroup(sampleWithNewData);

        const groups = service.getGroups(formGroup) as any;

        expect(groups).toMatchObject(sampleWithNewData);
      });

      it('should return NewGroups for empty Groups initial value', () => {
        const formGroup = service.createGroupsFormGroup();

        const groups = service.getGroups(formGroup) as any;

        expect(groups).toMatchObject({});
      });

      it('should return IGroups', () => {
        const formGroup = service.createGroupsFormGroup(sampleWithRequiredData);

        const groups = service.getGroups(formGroup) as any;

        expect(groups).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IGroups should not enable id FormControl', () => {
        const formGroup = service.createGroupsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewGroups should disable id FormControl', () => {
        const formGroup = service.createGroupsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
