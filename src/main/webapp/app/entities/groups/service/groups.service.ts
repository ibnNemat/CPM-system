import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGroups, NewGroups } from '../groups.model';

export type PartialUpdateGroups = Partial<IGroups> & Pick<IGroups, 'id'>;

export type EntityResponseType = HttpResponse<IGroups>;
export type EntityArrayResponseType = HttpResponse<IGroups[]>;

@Injectable({ providedIn: 'root' })
export class GroupsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/groups');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(groups: NewGroups): Observable<EntityResponseType> {
    return this.http.post<IGroups>(this.resourceUrl, groups, { observe: 'response' });
  }

  update(groups: IGroups): Observable<EntityResponseType> {
    return this.http.put<IGroups>(`${this.resourceUrl}/${this.getGroupsIdentifier(groups)}`, groups, { observe: 'response' });
  }

  partialUpdate(groups: PartialUpdateGroups): Observable<EntityResponseType> {
    return this.http.patch<IGroups>(`${this.resourceUrl}/${this.getGroupsIdentifier(groups)}`, groups, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGroups>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGroups[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getGroupsIdentifier(groups: Pick<IGroups, 'id'>): number {
    return groups.id;
  }

  compareGroups(o1: Pick<IGroups, 'id'> | null, o2: Pick<IGroups, 'id'> | null): boolean {
    return o1 && o2 ? this.getGroupsIdentifier(o1) === this.getGroupsIdentifier(o2) : o1 === o2;
  }

  addGroupsToCollectionIfMissing<Type extends Pick<IGroups, 'id'>>(
    groupsCollection: Type[],
    ...groupsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const groups: Type[] = groupsToCheck.filter(isPresent);
    if (groups.length > 0) {
      const groupsCollectionIdentifiers = groupsCollection.map(groupsItem => this.getGroupsIdentifier(groupsItem)!);
      const groupsToAdd = groups.filter(groupsItem => {
        const groupsIdentifier = this.getGroupsIdentifier(groupsItem);
        if (groupsCollectionIdentifiers.includes(groupsIdentifier)) {
          return false;
        }
        groupsCollectionIdentifiers.push(groupsIdentifier);
        return true;
      });
      return [...groupsToAdd, ...groupsCollection];
    }
    return groupsCollection;
  }
}
