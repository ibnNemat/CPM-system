import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BotTokenComponent } from './list/bot-token.component';
import { BotTokenDetailComponent } from './detail/bot-token-detail.component';
import { BotTokenUpdateComponent } from './update/bot-token-update.component';
import { BotTokenDeleteDialogComponent } from './delete/bot-token-delete-dialog.component';
import { BotTokenRoutingModule } from './route/bot-token-routing.module';

@NgModule({
  imports: [SharedModule, BotTokenRoutingModule],
  declarations: [BotTokenComponent, BotTokenDetailComponent, BotTokenUpdateComponent, BotTokenDeleteDialogComponent],
})
export class BotTokenModule {}
