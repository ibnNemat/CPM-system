import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPaymentHistory } from '../payment-history.model';
import { PaymentHistoryService } from '../service/payment-history.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './payment-history-delete-dialog.component.html',
})
export class PaymentHistoryDeleteDialogComponent {
  paymentHistory?: IPaymentHistory;

  constructor(protected paymentHistoryService: PaymentHistoryService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.paymentHistoryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
