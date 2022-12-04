import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { TelegramGroupFormService, TelegramGroupFormGroup } from './telegram-group-form.service';
import { ITelegramGroup } from '../telegram-group.model';
import { TelegramGroupService } from '../service/telegram-group.service';

@Component({
  selector: 'jhi-telegram-group-update',
  templateUrl: './telegram-group-update.component.html',
})
export class TelegramGroupUpdateComponent implements OnInit {
  isSaving = false;
  telegramGroup: ITelegramGroup | null = null;

  editForm: TelegramGroupFormGroup = this.telegramGroupFormService.createTelegramGroupFormGroup();

  constructor(
    protected telegramGroupService: TelegramGroupService,
    protected telegramGroupFormService: TelegramGroupFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ telegramGroup }) => {
      this.telegramGroup = telegramGroup;
      if (telegramGroup) {
        this.updateForm(telegramGroup);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const telegramGroup = this.telegramGroupFormService.getTelegramGroup(this.editForm);
    if (telegramGroup.id !== null) {
      this.subscribeToSaveResponse(this.telegramGroupService.update(telegramGroup));
    } else {
      this.subscribeToSaveResponse(this.telegramGroupService.create(telegramGroup));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITelegramGroup>>): void {
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

  protected updateForm(telegramGroup: ITelegramGroup): void {
    this.telegramGroup = telegramGroup;
    this.telegramGroupFormService.resetForm(this.editForm, telegramGroup);
  }
}
