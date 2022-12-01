import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CustomerTelegramComponent } from './list/customer-telegram.component';
import { CustomerTelegramDetailComponent } from './detail/customer-telegram-detail.component';
import { CustomerTelegramUpdateComponent } from './update/customer-telegram-update.component';
import { CustomerTelegramDeleteDialogComponent } from './delete/customer-telegram-delete-dialog.component';
import { CustomerTelegramRoutingModule } from './route/customer-telegram-routing.module';

@NgModule({
  imports: [SharedModule, CustomerTelegramRoutingModule],
  declarations: [
    CustomerTelegramComponent,
    CustomerTelegramDetailComponent,
    CustomerTelegramUpdateComponent,
    CustomerTelegramDeleteDialogComponent,
  ],
})
export class CustomerTelegramModule {}
