import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TelegramEntityFormService, TelegramEntityFormGroup } from './telegram-entity-form.service';
import { ITelegramEntity } from '../telegram-entity.model';
import { TelegramEntityService } from '../service/telegram-entity.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-telegram-entity-update',
  templateUrl: './telegram-entity-update.component.html',
})
export class TelegramEntityUpdateComponent implements OnInit {
  isSaving = false;
  telegramEntity: ITelegramEntity | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: TelegramEntityFormGroup = this.telegramEntityFormService.createTelegramEntityFormGroup();

  constructor(
    protected telegramEntityService: TelegramEntityService,
    protected telegramEntityFormService: TelegramEntityFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ telegramEntity }) => {
      this.telegramEntity = telegramEntity;
      if (telegramEntity) {
        this.updateForm(telegramEntity);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const telegramEntity = this.telegramEntityFormService.getTelegramEntity(this.editForm);
    if (telegramEntity.id !== null) {
      this.subscribeToSaveResponse(this.telegramEntityService.update(telegramEntity));
    } else {
      this.subscribeToSaveResponse(this.telegramEntityService.create(telegramEntity));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITelegramEntity>>): void {
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

  protected updateForm(telegramEntity: ITelegramEntity): void {
    this.telegramEntity = telegramEntity;
    this.telegramEntityFormService.resetForm(this.editForm, telegramEntity);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, telegramEntity.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.telegramEntity?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
