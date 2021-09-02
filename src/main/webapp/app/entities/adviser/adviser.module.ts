import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AdviserComponent } from './list/adviser.component';
import { AdviserDetailComponent } from './detail/adviser-detail.component';
import { AdviserUpdateComponent } from './update/adviser-update.component';
import { AdviserDeleteDialogComponent } from './delete/adviser-delete-dialog.component';
import { AdviserRoutingModule } from './route/adviser-routing.module';

@NgModule({
  imports: [SharedModule, AdviserRoutingModule],
  declarations: [AdviserComponent, AdviserDetailComponent, AdviserUpdateComponent, AdviserDeleteDialogComponent],
  entryComponents: [AdviserDeleteDialogComponent],
})
export class AdviserModule {}
