import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IServices } from '../services.model';

@Component({
  selector: 'jhi-services-detail',
  templateUrl: './services-detail.component.html',
})
export class ServicesDetailComponent implements OnInit {
  services: IServices | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ services }) => {
      this.services = services;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
