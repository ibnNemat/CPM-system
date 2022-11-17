import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { CustomersFormService, CustomersFormGroup } from './customers-form.service';
import { ICustomers } from '../customers.model';
import { CustomersService } from '../service/customers.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IGroups } from 'app/entities/groups/groups.model';
import { GroupsService } from 'app/entities/groups/service/groups.service';
import { IServices } from 'app/entities/services/services.model';
import { ServicesService } from 'app/entities/services/service/services.service';

@Component({
  selector: 'jhi-customers-update',
  templateUrl: './customers-update.component.html',
})
export class CustomersUpdateComponent implements OnInit {
  isSaving = false;
  customers: ICustomers | null = null;

  usersSharedCollection: IUser[] = [];
  groupsSharedCollection: IGroups[] = [];
  servicesSharedCollection: IServices[] = [];

  editForm: CustomersFormGroup = this.customersFormService.createCustomersFormGroup();

  constructor(
    protected customersService: CustomersService,
    protected customersFormService: CustomersFormService,
    protected userService: UserService,
    protected groupsService: GroupsService,
    protected servicesService: ServicesService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareGroups = (o1: IGroups | null, o2: IGroups | null): boolean => this.groupsService.compareGroups(o1, o2);

  compareServices = (o1: IServices | null, o2: IServices | null): boolean => this.servicesService.compareServices(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customers }) => {
      this.customers = customers;
      if (customers) {
        this.updateForm(customers);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const customers = this.customersFormService.getCustomers(this.editForm);
    if (customers.id !== null) {
      this.subscribeToSaveResponse(this.customersService.update(customers));
    } else {
      this.subscribeToSaveResponse(this.customersService.create(customers));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICustomers>>): void {
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

  protected updateForm(customers: ICustomers): void {
    this.customers = customers;
    this.customersFormService.resetForm(this.editForm, customers);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, customers.user);
    this.groupsSharedCollection = this.groupsService.addGroupsToCollectionIfMissing<IGroups>(
      this.groupsSharedCollection,
      ...(customers.groups ?? [])
    );
    this.servicesSharedCollection = this.servicesService.addServicesToCollectionIfMissing<IServices>(
      this.servicesSharedCollection,
      ...(customers.services ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.customers?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.groupsService
      .query()
      .pipe(map((res: HttpResponse<IGroups[]>) => res.body ?? []))
      .pipe(
        map((groups: IGroups[]) => this.groupsService.addGroupsToCollectionIfMissing<IGroups>(groups, ...(this.customers?.groups ?? [])))
      )
      .subscribe((groups: IGroups[]) => (this.groupsSharedCollection = groups));

    this.servicesService
      .query()
      .pipe(map((res: HttpResponse<IServices[]>) => res.body ?? []))
      .pipe(
        map((services: IServices[]) =>
          this.servicesService.addServicesToCollectionIfMissing<IServices>(services, ...(this.customers?.services ?? []))
        )
      )
      .subscribe((services: IServices[]) => (this.servicesSharedCollection = services));
  }
}
