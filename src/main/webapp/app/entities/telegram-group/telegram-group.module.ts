import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TelegramGroupComponent } from './list/telegram-group.component';
import { TelegramGroupDetailComponent } from './detail/telegram-group-detail.component';
import { TelegramGroupUpdateComponent } from './update/telegram-group-update.component';
import { TelegramGroupDeleteDialogComponent } from './delete/telegram-group-delete-dialog.component';
import { TelegramGroupRoutingModule } from './route/telegram-group-routing.module';

@NgModule({
  imports: [SharedModule, TelegramGroupRoutingModule],
  declarations: [TelegramGroupComponent, TelegramGroupDetailComponent, TelegramGroupUpdateComponent, TelegramGroupDeleteDialogComponent],
})
export class TelegramGroupModule {}
