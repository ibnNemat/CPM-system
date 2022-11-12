import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GroupsComponent } from '../list/groups.component';
import { GroupsDetailComponent } from '../detail/groups-detail.component';
import { GroupsUpdateComponent } from '../update/groups-update.component';
import { GroupsRoutingResolveService } from './groups-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const groupsRoute: Routes = [
  {
    path: '',
    component: GroupsComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GroupsDetailComponent,
    resolve: {
      groups: GroupsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GroupsUpdateComponent,
    resolve: {
      groups: GroupsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GroupsUpdateComponent,
    resolve: {
      groups: GroupsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(groupsRoute)],
  exports: [RouterModule],
})
export class GroupsRoutingModule {}
