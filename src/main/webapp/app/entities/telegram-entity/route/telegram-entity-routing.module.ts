import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TelegramEntityComponent } from '../list/telegram-entity.component';
import { TelegramEntityDetailComponent } from '../detail/telegram-entity-detail.component';
import { TelegramEntityUpdateComponent } from '../update/telegram-entity-update.component';
import { TelegramEntityRoutingResolveService } from './telegram-entity-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const telegramEntityRoute: Routes = [
  {
    path: '',
    component: TelegramEntityComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TelegramEntityDetailComponent,
    resolve: {
      telegramEntity: TelegramEntityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TelegramEntityUpdateComponent,
    resolve: {
      telegramEntity: TelegramEntityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TelegramEntityUpdateComponent,
    resolve: {
      telegramEntity: TelegramEntityRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(telegramEntityRoute)],
  exports: [RouterModule],
})
export class TelegramEntityRoutingModule {}
