import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IManager } from '../manager.model';

@Component({
  selector: 'jhi-manager-detail',
  templateUrl: './manager-detail.component.html',
})
export class ManagerDetailComponent implements OnInit {
  manager: IManager | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ manager }) => {
      this.manager = manager;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
