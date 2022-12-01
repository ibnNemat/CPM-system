import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICustomerTelegram } from '../customer-telegram.model';

@Component({
  selector: 'jhi-customer-telegram-detail',
  templateUrl: './customer-telegram-detail.component.html',
})
export class CustomerTelegramDetailComponent implements OnInit {
  customerTelegram: ICustomerTelegram | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customerTelegram }) => {
      this.customerTelegram = customerTelegram;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
