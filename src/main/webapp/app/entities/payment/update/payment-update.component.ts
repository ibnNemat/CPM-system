import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { PaymentFormService, PaymentFormGroup } from './payment-form.service';
import { IPayment } from '../payment.model';
import { PaymentService } from '../service/payment.service';
import { ICustomers } from 'app/entities/customers/customers.model';
import { CustomersService } from 'app/entities/customers/service/customers.service';
import { IServices } from 'app/entities/services/services.model';
import { ServicesService } from 'app/entities/services/service/services.service';
import { IGroups } from 'app/entities/groups/groups.model';
import { GroupsService } from 'app/entities/groups/service/groups.service';

@Component({
  selector: 'jhi-payment-update',
  templateUrl: './payment-update.component.html',
})
export class PaymentUpdateComponent implements OnInit {
  isSaving = false;
  payment: IPayment | null = null;

  customersSharedCollection: ICustomers[] = [];
  servicesSharedCollection: IServices[] = [];
  groupsSharedCollection: IGroups[] = [];

  editForm: PaymentFormGroup = this.paymentFormService.createPaymentFormGroup();

  constructor(
    protected paymentService: PaymentService,
    protected paymentFormService: PaymentFormService,
    protected customersService: CustomersService,
    protected servicesService: ServicesService,
    protected groupsService: GroupsService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareCustomers = (o1: ICustomers | null, o2: ICustomers | null): boolean => this.customersService.compareCustomers(o1, o2);

  compareServices = (o1: IServices | null, o2: IServices | null): boolean => this.servicesService.compareServices(o1, o2);

  compareGroups = (o1: IGroups | null, o2: IGroups | null): boolean => this.groupsService.compareGroups(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ payment }) => {
      this.payment = payment;
      if (payment) {
        this.updateForm(payment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const payment = this.paymentFormService.getPayment(this.editForm);
    if (payment.id !== null) {
      this.subscribeToSaveResponse(this.paymentService.update(payment));
    } else {
      this.subscribeToSaveResponse(this.paymentService.create(payment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPayment>>): void {
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

  protected updateForm(payment: IPayment): void {
    this.payment = payment;
    this.paymentFormService.resetForm(this.editForm, payment);

    this.customersSharedCollection = this.customersService.addCustomersToCollectionIfMissing<ICustomers>(
      this.customersSharedCollection,
      payment.customer
    );
    this.servicesSharedCollection = this.servicesService.addServicesToCollectionIfMissing<IServices>(
      this.servicesSharedCollection,
      payment.service
    );
    this.groupsSharedCollection = this.groupsService.addGroupsToCollectionIfMissing<IGroups>(this.groupsSharedCollection, payment.group);
  }

  protected loadRelationshipsOptions(): void {
    this.customersService
      .query()
      .pipe(map((res: HttpResponse<ICustomers[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomers[]) =>
          this.customersService.addCustomersToCollectionIfMissing<ICustomers>(customers, this.payment?.customer)
        )
      )
      .subscribe((customers: ICustomers[]) => (this.customersSharedCollection = customers));

    this.servicesService
      .query()
      .pipe(map((res: HttpResponse<IServices[]>) => res.body ?? []))
      .pipe(
        map((services: IServices[]) => this.servicesService.addServicesToCollectionIfMissing<IServices>(services, this.payment?.service))
      )
      .subscribe((services: IServices[]) => (this.servicesSharedCollection = services));

    this.groupsService
      .query()
      .pipe(map((res: HttpResponse<IGroups[]>) => res.body ?? []))
      .pipe(map((groups: IGroups[]) => this.groupsService.addGroupsToCollectionIfMissing<IGroups>(groups, this.payment?.group)))
      .subscribe((groups: IGroups[]) => (this.groupsSharedCollection = groups));
  }
}
