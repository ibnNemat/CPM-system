import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { GroupsComponent } from './list/groups.component';
import { GroupsDetailComponent } from './detail/groups-detail.component';
import { GroupsUpdateComponent } from './update/groups-update.component';
import { GroupsDeleteDialogComponent } from './delete/groups-delete-dialog.component';
import { GroupsRoutingModule } from './route/groups-routing.module';

@NgModule({
  imports: [SharedModule, GroupsRoutingModule],
  declarations: [GroupsComponent, GroupsDetailComponent, GroupsUpdateComponent, GroupsDeleteDialogComponent],
})
export class GroupsModule {}
