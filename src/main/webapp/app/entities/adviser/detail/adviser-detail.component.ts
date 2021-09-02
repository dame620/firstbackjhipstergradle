import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAdviser } from '../adviser.model';

@Component({
  selector: 'jhi-adviser-detail',
  templateUrl: './adviser-detail.component.html',
})
export class AdviserDetailComponent implements OnInit {
  adviser: IAdviser | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ adviser }) => {
      this.adviser = adviser;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
