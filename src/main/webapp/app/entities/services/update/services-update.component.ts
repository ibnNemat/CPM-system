import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ServicesFormService, ServicesFormGroup } from './services-form.service';
import { IServices } from '../services.model';
import { ServicesService } from '../service/services.service';
import { IGroups } from 'app/entities/groups/groups.model';
import { GroupsService } from 'app/entities/groups/service/groups.service';
import { ICustomers } from 'app/entities/customers/customers.model';
import { CustomersService } from 'app/entities/customers/service/customers.service';
import { ServiceType } from 'app/entities/enumerations/service-type.model';
import { PeriodType } from 'app/entities/enumerations/period-type.model';

@Component({
  selector: 'jhi-services-update',
  templateUrl: './services-update.component.html',
})
export class ServicesUpdateComponent implements OnInit {
  isSaving = false;
  services: IServices | null = null;
  serviceTypeValues = Object.keys(ServiceType);
  periodTypeValues = Object.keys(PeriodType);

  groupsSharedCollection: IGroups[] = [];
  customersSharedCollection: ICustomers[] = [];

  editForm: ServicesFormGroup = this.servicesFormService.createServicesFormGroup();

  constructor(
    protected servicesService: ServicesService,
    protected servicesFormService: ServicesFormService,
    protected groupsService: GroupsService,
    protected customersService: CustomersService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareGroups = (o1: IGroups | null, o2: IGroups | null): boolean => this.groupsService.compareGroups(o1, o2);

  compareCustomers = (o1: ICustomers | null, o2: ICustomers | null): boolean => this.customersService.compareCustomers(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ services }) => {
      this.services = services;
      if (services) {
        this.updateForm(services);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const services = this.servicesFormService.getServices(this.editForm);
    if (services.id !== null) {
      this.subscribeToSaveResponse(this.servicesService.update(services));
    } else {
      this.subscribeToSaveResponse(this.servicesService.create(services));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IServices>>): void {
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

  protected updateForm(services: IServices): void {
    this.services = services;
    this.servicesFormService.resetForm(this.editForm, services);

    this.groupsSharedCollection = this.groupsService.addGroupsToCollectionIfMissing<IGroups>(this.groupsSharedCollection, services.group);
    this.customersSharedCollection = this.customersService.addCustomersToCollectionIfMissing<ICustomers>(
      this.customersSharedCollection,
      ...(services.users ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.groupsService
      .query()
      .pipe(map((res: HttpResponse<IGroups[]>) => res.body ?? []))
      .pipe(map((groups: IGroups[]) => this.groupsService.addGroupsToCollectionIfMissing<IGroups>(groups, this.services?.group)))
      .subscribe((groups: IGroups[]) => (this.groupsSharedCollection = groups));

    this.customersService
      .query()
      .pipe(map((res: HttpResponse<ICustomers[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomers[]) =>
          this.customersService.addCustomersToCollectionIfMissing<ICustomers>(customers, ...(this.services?.users ?? []))
        )
      )
      .subscribe((customers: ICustomers[]) => (this.customersSharedCollection = customers));
  }
}
