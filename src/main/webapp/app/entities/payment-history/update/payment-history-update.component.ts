import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { PaymentHistoryFormService, PaymentHistoryFormGroup } from './payment-history-form.service';
import { IPaymentHistory } from '../payment-history.model';
import { PaymentHistoryService } from '../service/payment-history.service';

@Component({
  selector: 'jhi-payment-history-update',
  templateUrl: './payment-history-update.component.html',
})
export class PaymentHistoryUpdateComponent implements OnInit {
  isSaving = false;
  paymentHistory: IPaymentHistory | null = null;

  editForm: PaymentHistoryFormGroup = this.paymentHistoryFormService.createPaymentHistoryFormGroup();

  constructor(
    protected paymentHistoryService: PaymentHistoryService,
    protected paymentHistoryFormService: PaymentHistoryFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paymentHistory }) => {
      this.paymentHistory = paymentHistory;
      if (paymentHistory) {
        this.updateForm(paymentHistory);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const paymentHistory = this.paymentHistoryFormService.getPaymentHistory(this.editForm);
    if (paymentHistory.id !== null) {
      this.subscribeToSaveResponse(this.paymentHistoryService.update(paymentHistory));
    } else {
      this.subscribeToSaveResponse(this.paymentHistoryService.create(paymentHistory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPaymentHistory>>): void {
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

  protected updateForm(paymentHistory: IPaymentHistory): void {
    this.paymentHistory = paymentHistory;
    this.paymentHistoryFormService.resetForm(this.editForm, paymentHistory);
  }
}
