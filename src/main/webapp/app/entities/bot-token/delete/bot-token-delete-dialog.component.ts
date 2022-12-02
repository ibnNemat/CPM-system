import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBotToken } from '../bot-token.model';
import { BotTokenService } from '../service/bot-token.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './bot-token-delete-dialog.component.html',
})
export class BotTokenDeleteDialogComponent {
  botToken?: IBotToken;

  constructor(protected botTokenService: BotTokenService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.botTokenService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
