import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAdviser, Adviser } from '../adviser.model';
import { AdviserService } from '../service/adviser.service';

@Injectable({ providedIn: 'root' })
export class AdviserRoutingResolveService implements Resolve<IAdviser> {
  constructor(protected service: AdviserService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAdviser> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((adviser: HttpResponse<Adviser>) => {
          if (adviser.body) {
            return of(adviser.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Adviser());
  }
}
