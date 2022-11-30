import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITelegramEntity } from '../telegram-entity.model';

@Component({
  selector: 'jhi-telegram-entity-detail',
  templateUrl: './telegram-entity-detail.component.html',
})
export class TelegramEntityDetailComponent implements OnInit {
  telegramEntity: ITelegramEntity | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ telegramEntity }) => {
      this.telegramEntity = telegramEntity;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
