import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AdviserComponent } from '../list/adviser.component';
import { AdviserDetailComponent } from '../detail/adviser-detail.component';
import { AdviserUpdateComponent } from '../update/adviser-update.component';
import { AdviserRoutingResolveService } from './adviser-routing-resolve.service';

const adviserRoute: Routes = [
  {
    path: '',
    component: AdviserComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AdviserDetailComponent,
    resolve: {
      adviser: AdviserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AdviserUpdateComponent,
    resolve: {
      adviser: AdviserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AdviserUpdateComponent,
    resolve: {
      adviser: AdviserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(adviserRoute)],
  exports: [RouterModule],
})
export class AdviserRoutingModule {}
