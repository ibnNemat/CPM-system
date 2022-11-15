import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { GroupsFormService, GroupsFormGroup } from './groups-form.service';
import { IGroups } from '../groups.model';
import { GroupsService } from '../service/groups.service';
import { IServices } from 'app/entities/services/services.model';
import { ServicesService } from 'app/entities/services/service/services.service';
import { IOrganization } from 'app/entities/organization/organization.model';
import { OrganizationService } from 'app/entities/organization/service/organization.service';

@Component({
  selector: 'jhi-groups-update',
  templateUrl: './groups-update.component.html',
})
export class GroupsUpdateComponent implements OnInit {
  isSaving = false;
  groups: IGroups | null = null;

  servicesSharedCollection: IServices[] = [];
  organizationsSharedCollection: IOrganization[] = [];

  editForm: GroupsFormGroup = this.groupsFormService.createGroupsFormGroup();

  constructor(
    protected groupsService: GroupsService,
    protected groupsFormService: GroupsFormService,
    protected servicesService: ServicesService,
    protected organizationService: OrganizationService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareServices = (o1: IServices | null, o2: IServices | null): boolean => this.servicesService.compareServices(o1, o2);

  compareOrganization = (o1: IOrganization | null, o2: IOrganization | null): boolean =>
    this.organizationService.compareOrganization(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ groups }) => {
      this.groups = groups;
      if (groups) {
        this.updateForm(groups);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const groups = this.groupsFormService.getGroups(this.editForm);
    if (groups.id !== null) {
      this.subscribeToSaveResponse(this.groupsService.update(groups));
    } else {
      this.subscribeToSaveResponse(this.groupsService.create(groups));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGroups>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(groups: IGroups): void {
    this.groups = groups;
    this.groupsFormService.resetForm(this.editForm, groups);

    this.servicesSharedCollection = this.servicesService.addServicesToCollectionIfMissing<IServices>(
      this.servicesSharedCollection,
      ...(groups.services ?? [])
    );
    this.organizationsSharedCollection = this.organizationService.addOrganizationToCollectionIfMissing<IOrganization>(
      this.organizationsSharedCollection,
      groups.organization
    );
  }

  protected loadRelationshipsOptions(): void {
    this.servicesService
      .query()
      .pipe(map((res: HttpResponse<IServices[]>) => res.body ?? []))
      .pipe(
        map((services: IServices[]) =>
          this.servicesService.addServicesToCollectionIfMissing<IServices>(services, ...(this.groups?.services ?? []))
        )
      )
      .subscribe((services: IServices[]) => (this.servicesSharedCollection = services));

    this.organizationService
      .query()
      .pipe(map((res: HttpResponse<IOrganization[]>) => res.body ?? []))
      .pipe(
        map((organizations: IOrganization[]) =>
          this.organizationService.addOrganizationToCollectionIfMissing<IOrganization>(organizations, this.groups?.organization)
        )
      )
      .subscribe((organizations: IOrganization[]) => (this.organizationsSharedCollection = organizations));
  }
}
