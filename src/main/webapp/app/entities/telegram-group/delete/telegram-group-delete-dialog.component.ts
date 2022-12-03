import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITelegramGroup } from '../telegram-group.model';
import { TelegramGroupService } from '../service/telegram-group.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './telegram-group-delete-dialog.component.html',
})
export class TelegramGroupDeleteDialogComponent {
  telegramGroup?: ITelegramGroup;

  constructor(protected telegramGroupService: TelegramGroupService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.telegramGroupService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
