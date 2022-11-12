import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ServicesComponent } from '../list/services.component';
import { ServicesDetailComponent } from '../detail/services-detail.component';
import { ServicesUpdateComponent } from '../update/services-update.component';
import { ServicesRoutingResolveService } from './services-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const servicesRoute: Routes = [
  {
    path: '',
    component: ServicesComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ServicesDetailComponent,
    resolve: {
      services: ServicesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ServicesUpdateComponent,
    resolve: {
      services: ServicesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ServicesUpdateComponent,
    resolve: {
      services: ServicesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(servicesRoute)],
  exports: [RouterModule],
})
export class ServicesRoutingModule {}
