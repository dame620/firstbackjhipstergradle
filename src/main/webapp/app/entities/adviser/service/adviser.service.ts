import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAdviser, getAdviserIdentifier } from '../adviser.model';

export type EntityResponseType = HttpResponse<IAdviser>;
export type EntityArrayResponseType = HttpResponse<IAdviser[]>;

@Injectable({ providedIn: 'root' })
export class AdviserService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/advisers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(adviser: IAdviser): Observable<EntityResponseType> {
    return this.http.post<IAdviser>(this.resourceUrl, adviser, { observe: 'response' });
  }

  update(adviser: IAdviser): Observable<EntityResponseType> {
    return this.http.put<IAdviser>(`${this.resourceUrl}/${getAdviserIdentifier(adviser) as number}`, adviser, { observe: 'response' });
  }

  partialUpdate(adviser: IAdviser): Observable<EntityResponseType> {
    return this.http.patch<IAdviser>(`${this.resourceUrl}/${getAdviserIdentifier(adviser) as number}`, adviser, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAdviser>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAdviser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addAdviserToCollectionIfMissing(adviserCollection: IAdviser[], ...advisersToCheck: (IAdviser | null | undefined)[]): IAdviser[] {
    const advisers: IAdviser[] = advisersToCheck.filter(isPresent);
    if (advisers.length > 0) {
      const adviserCollectionIdentifiers = adviserCollection.map(adviserItem => getAdviserIdentifier(adviserItem)!);
      const advisersToAdd = advisers.filter(adviserItem => {
        const adviserIdentifier = getAdviserIdentifier(adviserItem);
        if (adviserIdentifier == null || adviserCollectionIdentifiers.includes(adviserIdentifier)) {
          return false;
        }
        adviserCollectionIdentifiers.push(adviserIdentifier);
        return true;
      });
      return [...advisersToAdd, ...adviserCollection];
    }
    return adviserCollection;
  }
}
