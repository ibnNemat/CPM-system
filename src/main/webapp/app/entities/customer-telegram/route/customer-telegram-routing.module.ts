import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CustomerTelegramComponent } from '../list/customer-telegram.component';
import { CustomerTelegramDetailComponent } from '../detail/customer-telegram-detail.component';
import { CustomerTelegramUpdateComponent } from '../update/customer-telegram-update.component';
import { CustomerTelegramRoutingResolveService } from './customer-telegram-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const customerTelegramRoute: Routes = [
  {
    path: '',
    component: CustomerTelegramComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CustomerTelegramDetailComponent,
    resolve: {
      customerTelegram: CustomerTelegramRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CustomerTelegramUpdateComponent,
    resolve: {
      customerTelegram: CustomerTelegramRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CustomerTelegramUpdateComponent,
    resolve: {
      customerTelegram: CustomerTelegramRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(customerTelegramRoute)],
  exports: [RouterModule],
})
export class CustomerTelegramRoutingModule {}
