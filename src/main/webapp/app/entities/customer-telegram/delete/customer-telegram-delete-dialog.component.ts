import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICustomerTelegram } from '../customer-telegram.model';
import { CustomerTelegramService } from '../service/customer-telegram.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './customer-telegram-delete-dialog.component.html',
})
export class CustomerTelegramDeleteDialogComponent {
  customerTelegram?: ICustomerTelegram;

  constructor(protected customerTelegramService: CustomerTelegramService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.customerTelegramService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
