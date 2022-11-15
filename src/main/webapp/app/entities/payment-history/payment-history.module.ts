import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PaymentHistoryComponent } from './list/payment-history.component';
import { PaymentHistoryDetailComponent } from './detail/payment-history-detail.component';
import { PaymentHistoryUpdateComponent } from './update/payment-history-update.component';
import { PaymentHistoryDeleteDialogComponent } from './delete/payment-history-delete-dialog.component';
import { PaymentHistoryRoutingModule } from './route/payment-history-routing.module';

@NgModule({
  imports: [SharedModule, PaymentHistoryRoutingModule],
  declarations: [
    PaymentHistoryComponent,
    PaymentHistoryDetailComponent,
    PaymentHistoryUpdateComponent,
    PaymentHistoryDeleteDialogComponent,
  ],
})
export class PaymentHistoryModule {}
