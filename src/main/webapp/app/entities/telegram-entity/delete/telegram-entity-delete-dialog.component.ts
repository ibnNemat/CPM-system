import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITelegramEntity } from '../telegram-entity.model';
import { TelegramEntityService } from '../service/telegram-entity.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './telegram-entity-delete-dialog.component.html',
})
export class TelegramEntityDeleteDialogComponent {
  telegramEntity?: ITelegramEntity;

  constructor(protected telegramEntityService: TelegramEntityService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.telegramEntityService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
