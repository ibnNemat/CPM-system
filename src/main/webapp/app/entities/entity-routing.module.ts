import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'customers',
        data: { pageTitle: 'cpmSystemApp.customers.home.title' },
        loadChildren: () => import('./customers/customers.module').then(m => m.CustomersModule),
      },
      {
        path: 'role',
        data: { pageTitle: 'cpmSystemApp.role.home.title' },
        loadChildren: () => import('./role/role.module').then(m => m.RoleModule),
      },
      {
        path: 'organization',
        data: { pageTitle: 'cpmSystemApp.organization.home.title' },
        loadChildren: () => import('./organization/organization.module').then(m => m.OrganizationModule),
      },
      {
        path: 'groups',
        data: { pageTitle: 'cpmSystemApp.groups.home.title' },
        loadChildren: () => import('./groups/groups.module').then(m => m.GroupsModule),
      },
      {
        path: 'services',
        data: { pageTitle: 'cpmSystemApp.services.home.title' },
        loadChildren: () => import('./services/services.module').then(m => m.ServicesModule),
      },
      {
        path: 'payment',
        data: { pageTitle: 'cpmSystemApp.payment.home.title' },
        loadChildren: () => import('./payment/payment.module').then(m => m.PaymentModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
