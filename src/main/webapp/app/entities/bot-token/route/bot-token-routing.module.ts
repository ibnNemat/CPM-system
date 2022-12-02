import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BotTokenComponent } from '../list/bot-token.component';
import { BotTokenDetailComponent } from '../detail/bot-token-detail.component';
import { BotTokenUpdateComponent } from '../update/bot-token-update.component';
import { BotTokenRoutingResolveService } from './bot-token-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const botTokenRoute: Routes = [
  {
    path: '',
    component: BotTokenComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BotTokenDetailComponent,
    resolve: {
      botToken: BotTokenRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BotTokenUpdateComponent,
    resolve: {
      botToken: BotTokenRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BotTokenUpdateComponent,
    resolve: {
      botToken: BotTokenRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(botTokenRoute)],
  exports: [RouterModule],
})
export class BotTokenRoutingModule {}
