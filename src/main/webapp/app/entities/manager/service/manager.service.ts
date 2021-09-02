import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IManager, getManagerIdentifier } from '../manager.model';

export type EntityResponseType = HttpResponse<IManager>;
export type EntityArrayResponseType = HttpResponse<IManager[]>;

@Injectable({ providedIn: 'root' })
export class ManagerService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/managers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(manager: IManager): Observable<EntityResponseType> {
    return this.http.post<IManager>(this.resourceUrl, manager, { observe: 'response' });
  }

  update(manager: IManager): Observable<EntityResponseType> {
    return this.http.put<IManager>(`${this.resourceUrl}/${getManagerIdentifier(manager) as number}`, manager, { observe: 'response' });
  }

  partialUpdate(manager: IManager): Observable<EntityResponseType> {
    return this.http.patch<IManager>(`${this.resourceUrl}/${getManagerIdentifier(manager) as number}`, manager, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IManager>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IManager[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addManagerToCollectionIfMissing(managerCollection: IManager[], ...managersToCheck: (IManager | null | undefined)[]): IManager[] {
    const managers: IManager[] = managersToCheck.filter(isPresent);
    if (managers.length > 0) {
      const managerCollectionIdentifiers = managerCollection.map(managerItem => getManagerIdentifier(managerItem)!);
      const managersToAdd = managers.filter(managerItem => {
        const managerIdentifier = getManagerIdentifier(managerItem);
        if (managerIdentifier == null || managerCollectionIdentifiers.includes(managerIdentifier)) {
          return false;
        }
        managerCollectionIdentifiers.push(managerIdentifier);
        return true;
      });
      return [...managersToAdd, ...managerCollection];
    }
    return managerCollection;
  }
}
