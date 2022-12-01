import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { CustomerTelegramFormService, CustomerTelegramFormGroup } from './customer-telegram-form.service';
import { ICustomerTelegram } from '../customer-telegram.model';
import { CustomerTelegramService } from '../service/customer-telegram.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-customer-telegram-update',
  templateUrl: './customer-telegram-update.component.html',
})
export class CustomerTelegramUpdateComponent implements OnInit {
  isSaving = false;
  customerTelegram: ICustomerTelegram | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: CustomerTelegramFormGroup = this.customerTelegramFormService.createCustomerTelegramFormGroup();

  constructor(
    protected customerTelegramService: CustomerTelegramService,
    protected customerTelegramFormService: CustomerTelegramFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

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

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, customerTelegram.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.customerTelegram?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
