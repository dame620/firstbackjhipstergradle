import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ManagerComponent } from '../list/manager.component';
import { ManagerDetailComponent } from '../detail/manager-detail.component';
import { ManagerUpdateComponent } from '../update/manager-update.component';
import { ManagerRoutingResolveService } from './manager-routing-resolve.service';

const managerRoute: Routes = [
  {
    path: '',
    component: ManagerComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ManagerDetailComponent,
    resolve: {
      manager: ManagerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ManagerUpdateComponent,
    resolve: {
      manager: ManagerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ManagerUpdateComponent,
    resolve: {
      manager: ManagerRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(managerRoute)],
  exports: [RouterModule],
})
export class ManagerRoutingModule {}
