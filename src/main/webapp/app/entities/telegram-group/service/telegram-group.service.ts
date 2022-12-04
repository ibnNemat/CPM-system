import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITelegramGroup, NewTelegramGroup } from '../telegram-group.model';

export type PartialUpdateTelegramGroup = Partial<ITelegramGroup> & Pick<ITelegramGroup, 'id'>;

export type EntityResponseType = HttpResponse<ITelegramGroup>;
export type EntityArrayResponseType = HttpResponse<ITelegramGroup[]>;

@Injectable({ providedIn: 'root' })
export class TelegramGroupService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/telegram-groups');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(telegramGroup: NewTelegramGroup): Observable<EntityResponseType> {
    return this.http.post<ITelegramGroup>(this.resourceUrl, telegramGroup, { observe: 'response' });
  }

  update(telegramGroup: ITelegramGroup): Observable<EntityResponseType> {
    return this.http.put<ITelegramGroup>(`${this.resourceUrl}/${this.getTelegramGroupIdentifier(telegramGroup)}`, telegramGroup, {
      observe: 'response',
    });
  }

  partialUpdate(telegramGroup: PartialUpdateTelegramGroup): Observable<EntityResponseType> {
    return this.http.patch<ITelegramGroup>(`${this.resourceUrl}/${this.getTelegramGroupIdentifier(telegramGroup)}`, telegramGroup, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITelegramGroup>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITelegramGroup[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTelegramGroupIdentifier(telegramGroup: Pick<ITelegramGroup, 'id'>): number {
    return telegramGroup.id;
  }

  compareTelegramGroup(o1: Pick<ITelegramGroup, 'id'> | null, o2: Pick<ITelegramGroup, 'id'> | null): boolean {
    return o1 && o2 ? this.getTelegramGroupIdentifier(o1) === this.getTelegramGroupIdentifier(o2) : o1 === o2;
  }

  addTelegramGroupToCollectionIfMissing<Type extends Pick<ITelegramGroup, 'id'>>(
    telegramGroupCollection: Type[],
    ...telegramGroupsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const telegramGroups: Type[] = telegramGroupsToCheck.filter(isPresent);
    if (telegramGroups.length > 0) {
      const telegramGroupCollectionIdentifiers = telegramGroupCollection.map(
        telegramGroupItem => this.getTelegramGroupIdentifier(telegramGroupItem)!
      );
      const telegramGroupsToAdd = telegramGroups.filter(telegramGroupItem => {
        const telegramGroupIdentifier = this.getTelegramGroupIdentifier(telegramGroupItem);
        if (telegramGroupCollectionIdentifiers.includes(telegramGroupIdentifier)) {
          return false;
        }
        telegramGroupCollectionIdentifiers.push(telegramGroupIdentifier);
        return true;
      });
      return [...telegramGroupsToAdd, ...telegramGroupCollection];
    }
    return telegramGroupCollection;
  }
}
