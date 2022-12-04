import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { CustomerTelegramFormService, CustomerTelegramFormGroup } from './customer-telegram-form.service';
import { ICustomerTelegram } from '../customer-telegram.model';
import { CustomerTelegramService } from '../service/customer-telegram.service';
import { ICustomers } from 'app/entities/customers/customers.model';
import { CustomersService } from 'app/entities/customers/service/customers.service';
import { ITelegramGroup } from 'app/entities/telegram-group/telegram-group.model';
import { TelegramGroupService } from 'app/entities/telegram-group/service/telegram-group.service';

@Component({
  selector: 'jhi-customer-telegram-update',
  templateUrl: './customer-telegram-update.component.html',
})
export class CustomerTelegramUpdateComponent implements OnInit {
  isSaving = false;
  customerTelegram: ICustomerTelegram | null = null;

  customersSharedCollection: ICustomers[] = [];
  telegramGroupsSharedCollection: ITelegramGroup[] = [];

  editForm: CustomerTelegramFormGroup = this.customerTelegramFormService.createCustomerTelegramFormGroup();

  constructor(
    protected customerTelegramService: CustomerTelegramService,
    protected customerTelegramFormService: CustomerTelegramFormService,
    protected customersService: CustomersService,
    protected telegramGroupService: TelegramGroupService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareCustomers = (o1: ICustomers | null, o2: ICustomers | null): boolean => this.customersService.compareCustomers(o1, o2);

  compareTelegramGroup = (o1: ITelegramGroup | null, o2: ITelegramGroup | null): boolean =>
    this.telegramGroupService.compareTelegramGroup(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customerTelegram }) => {
      this.customerTelegram = customerTelegram;
      if (customerTelegram) {
        this.updateForm(customerTelegram);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const customerTelegram = this.customerTelegramFormService.getCustomerTelegram(this.editForm);
    if (customerTelegram.id !== null) {
      this.subscribeToSaveResponse(this.customerTelegramService.update(customerTelegram));
    } else {
      this.subscribeToSaveResponse(this.customerTelegramService.create(customerTelegram));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICustomerTelegram>>): void {
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

  protected updateForm(customerTelegram: ICustomerTelegram): void {
    this.customerTelegram = customerTelegram;
    this.customerTelegramFormService.resetForm(this.editForm, customerTelegram);

    this.customersSharedCollection = this.customersService.addCustomersToCollectionIfMissing<ICustomers>(
      this.customersSharedCollection,
      customerTelegram.customer
    );
    this.telegramGroupsSharedCollection = this.telegramGroupService.addTelegramGroupToCollectionIfMissing<ITelegramGroup>(
      this.telegramGroupsSharedCollection,
      ...(customerTelegram.telegramGroups ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.customersService
      .query()
      .pipe(map((res: HttpResponse<ICustomers[]>) => res.body ?? []))
      .pipe(
        map((customers: ICustomers[]) =>
          this.customersService.addCustomersToCollectionIfMissing<ICustomers>(customers, this.customerTelegram?.customer)
        )
      )
      .subscribe((customers: ICustomers[]) => (this.customersSharedCollection = customers));

    this.telegramGroupService
      .query()
      .pipe(map((res: HttpResponse<ITelegramGroup[]>) => res.body ?? []))
      .pipe(
        map((telegramGroups: ITelegramGroup[]) =>
          this.telegramGroupService.addTelegramGroupToCollectionIfMissing<ITelegramGroup>(
            telegramGroups,
            ...(this.customerTelegram?.telegramGroups ?? [])
          )
        )
      )
      .subscribe((telegramGroups: ITelegramGroup[]) => (this.telegramGroupsSharedCollection = telegramGroups));
  }
}
