import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CustomersComponent } from './list/customers.component';
import { CustomersDetailComponent } from './detail/customers-detail.component';
import { CustomersUpdateComponent } from './update/customers-update.component';
import { CustomersDeleteDialogComponent } from './delete/customers-delete-dialog.component';
import { CustomersRoutingModule } from './route/customers-routing.module';

@NgModule({
  imports: [SharedModule, CustomersRoutingModule],
  declarations: [CustomersComponent, CustomersDetailComponent, CustomersUpdateComponent, CustomersDeleteDialogComponent],
})
export class CustomersModule {}
