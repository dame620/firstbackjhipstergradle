import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'appointment',
        data: { pageTitle: 'appointementsystemApp.appointment.home.title' },
        loadChildren: () => import('./appointment/appointment.module').then(m => m.AppointmentModule),
      },
      {
        path: 'manager',
        data: { pageTitle: 'appointementsystemApp.manager.home.title' },
        loadChildren: () => import('./manager/manager.module').then(m => m.ManagerModule),
      },
      {
        path: 'company',
        data: { pageTitle: 'appointementsystemApp.company.home.title' },
        loadChildren: () => import('./company/company.module').then(m => m.CompanyModule),
      },
      {
        path: 'bank',
        data: { pageTitle: 'appointementsystemApp.bank.home.title' },
        loadChildren: () => import('./bank/bank.module').then(m => m.BankModule),
      },
      {
        path: 'adviser',
        data: { pageTitle: 'appointementsystemApp.adviser.home.title' },
        loadChildren: () => import('./adviser/adviser.module').then(m => m.AdviserModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
