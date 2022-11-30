import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TelegramEntityComponent } from './list/telegram-entity.component';
import { TelegramEntityDetailComponent } from './detail/telegram-entity-detail.component';
import { TelegramEntityUpdateComponent } from './update/telegram-entity-update.component';
import { TelegramEntityDeleteDialogComponent } from './delete/telegram-entity-delete-dialog.component';
import { TelegramEntityRoutingModule } from './route/telegram-entity-routing.module';

@NgModule({
  imports: [SharedModule, TelegramEntityRoutingModule],
  declarations: [
    TelegramEntityComponent,
    TelegramEntityDetailComponent,
    TelegramEntityUpdateComponent,
    TelegramEntityDeleteDialogComponent,
  ],
})
export class TelegramEntityModule {}
