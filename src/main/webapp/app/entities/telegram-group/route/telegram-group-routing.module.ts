import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TelegramGroupComponent } from '../list/telegram-group.component';
import { TelegramGroupDetailComponent } from '../detail/telegram-group-detail.component';
import { TelegramGroupUpdateComponent } from '../update/telegram-group-update.component';
import { TelegramGroupRoutingResolveService } from './telegram-group-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const telegramGroupRoute: Routes = [
  {
    path: '',
    component: TelegramGroupComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TelegramGroupDetailComponent,
    resolve: {
      telegramGroup: TelegramGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TelegramGroupUpdateComponent,
    resolve: {
      telegramGroup: TelegramGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TelegramGroupUpdateComponent,
    resolve: {
      telegramGroup: TelegramGroupRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(telegramGroupRoute)],
  exports: [RouterModule],
})
export class TelegramGroupRoutingModule {}
