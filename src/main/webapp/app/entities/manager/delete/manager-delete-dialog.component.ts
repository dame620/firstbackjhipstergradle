import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IManager } from '../manager.model';
import { ManagerService } from '../service/manager.service';

@Component({
  templateUrl: './manager-delete-dialog.component.html',
})
export class ManagerDeleteDialogComponent {
  manager?: IManager;

  constructor(protected managerService: ManagerService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.managerService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
