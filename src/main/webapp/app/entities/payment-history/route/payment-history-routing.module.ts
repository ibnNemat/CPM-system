import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PaymentHistoryComponent } from '../list/payment-history.component';
import { PaymentHistoryDetailComponent } from '../detail/payment-history-detail.component';
import { PaymentHistoryUpdateComponent } from '../update/payment-history-update.component';
import { PaymentHistoryRoutingResolveService } from './payment-history-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const paymentHistoryRoute: Routes = [
  {
    path: '',
    component: PaymentHistoryComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PaymentHistoryDetailComponent,
    resolve: {
      paymentHistory: PaymentHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PaymentHistoryUpdateComponent,
    resolve: {
      paymentHistory: PaymentHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PaymentHistoryUpdateComponent,
    resolve: {
      paymentHistory: PaymentHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(paymentHistoryRoute)],
  exports: [RouterModule],
})
export class PaymentHistoryRoutingModule {}
