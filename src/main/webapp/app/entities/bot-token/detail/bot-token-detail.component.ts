import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBotToken } from '../bot-token.model';

@Component({
  selector: 'jhi-bot-token-detail',
  templateUrl: './bot-token-detail.component.html',
})
export class BotTokenDetailComponent implements OnInit {
  botToken: IBotToken | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ botToken }) => {
      this.botToken = botToken;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
