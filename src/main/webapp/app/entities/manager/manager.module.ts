import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ManagerComponent } from './list/manager.component';
import { ManagerDetailComponent } from './detail/manager-detail.component';
import { ManagerUpdateComponent } from './update/manager-update.component';
import { ManagerDeleteDialogComponent } from './delete/manager-delete-dialog.component';
import { ManagerRoutingModule } from './route/manager-routing.module';

@NgModule({
  imports: [SharedModule, ManagerRoutingModule],
  declarations: [ManagerComponent, ManagerDetailComponent, ManagerUpdateComponent, ManagerDeleteDialogComponent],
  entryComponents: [ManagerDeleteDialogComponent],
})
export class ManagerModule {}
