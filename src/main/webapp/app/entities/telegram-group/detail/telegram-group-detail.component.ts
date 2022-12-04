import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITelegramGroup } from '../telegram-group.model';

@Component({
  selector: 'jhi-telegram-group-detail',
  templateUrl: './telegram-group-detail.component.html',
})
export class TelegramGroupDetailComponent implements OnInit {
  telegramGroup: ITelegramGroup | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ telegramGroup }) => {
      this.telegramGroup = telegramGroup;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
