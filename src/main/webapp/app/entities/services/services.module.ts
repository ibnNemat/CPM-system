import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ServicesComponent } from './list/services.component';
import { ServicesDetailComponent } from './detail/services-detail.component';
import { ServicesUpdateComponent } from './update/services-update.component';
import { ServicesDeleteDialogComponent } from './delete/services-delete-dialog.component';
import { ServicesRoutingModule } from './route/services-routing.module';

@NgModule({
  imports: [SharedModule, ServicesRoutingModule],
  declarations: [ServicesComponent, ServicesDetailComponent, ServicesUpdateComponent, ServicesDeleteDialogComponent],
})
export class ServicesModule {}
