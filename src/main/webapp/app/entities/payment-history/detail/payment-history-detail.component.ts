import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPaymentHistory } from '../payment-history.model';

@Component({
  selector: 'jhi-payment-history-detail',
  templateUrl: './payment-history-detail.component.html',
})
export class PaymentHistoryDetailComponent implements OnInit {
  paymentHistory: IPaymentHistory | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paymentHistory }) => {
      this.paymentHistory = paymentHistory;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
