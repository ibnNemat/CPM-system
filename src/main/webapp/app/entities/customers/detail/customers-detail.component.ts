import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICustomers } from '../customers.model';

@Component({
  selector: 'jhi-customers-detail',
  templateUrl: './customers-detail.component.html',
})
export class CustomersDetailComponent implements OnInit {
  customers: ICustomers | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customers }) => {
      this.customers = customers;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
