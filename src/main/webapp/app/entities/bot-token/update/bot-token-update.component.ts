import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { BotTokenFormService, BotTokenFormGroup } from './bot-token-form.service';
import { IBotToken } from '../bot-token.model';
import { BotTokenService } from '../service/bot-token.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-bot-token-update',
  templateUrl: './bot-token-update.component.html',
})
export class BotTokenUpdateComponent implements OnInit {
  isSaving = false;
  botToken: IBotToken | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: BotTokenFormGroup = this.botTokenFormService.createBotTokenFormGroup();

  constructor(
    protected botTokenService: BotTokenService,
    protected botTokenFormService: BotTokenFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ botToken }) => {
      this.botToken = botToken;
      if (botToken) {
        this.updateForm(botToken);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const botToken = this.botTokenFormService.getBotToken(this.editForm);
    if (botToken.id !== null) {
      this.subscribeToSaveResponse(this.botTokenService.update(botToken));
    } else {
      this.subscribeToSaveResponse(this.botTokenService.create(botToken));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBotToken>>): void {
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

  protected updateForm(botToken: IBotToken): void {
    this.botToken = botToken;
    this.botTokenFormService.resetForm(this.editForm, botToken);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, botToken.createdBy);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.botToken?.createdBy)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
