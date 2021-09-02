import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAdviser } from '../adviser.model';
import { AdviserService } from '../service/adviser.service';

@Component({
  templateUrl: './adviser-delete-dialog.component.html',
})
export class AdviserDeleteDialogComponent {
  adviser?: IAdviser;

  constructor(protected adviserService: AdviserService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.adviserService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
