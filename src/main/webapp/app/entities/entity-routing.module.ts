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
      {
        path: 'payment-history',
        data: { pageTitle: 'cpmSystemApp.paymentHistory.home.title' },
        loadChildren: () => import('./payment-history/payment-history.module').then(m => m.PaymentHistoryModule),
      },
      {
        path: 'customer-telegram',
        data: { pageTitle: 'cpmSystemApp.customerTelegram.home.title' },
        loadChildren: () => import('./customer-telegram/customer-telegram.module').then(m => m.CustomerTelegramModule),
      },
      {
        path: 'bot-token',
        data: { pageTitle: 'cpmSystemApp.botToken.home.title' },
        loadChildren: () => import('./bot-token/bot-token.module').then(m => m.BotTokenModule),
      },
      {
        path: 'telegram-group',
        data: { pageTitle: 'cpmSystemApp.telegramGroup.home.title' },
        loadChildren: () => import('./telegram-group/telegram-group.module').then(m => m.TelegramGroupModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
