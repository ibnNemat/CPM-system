import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITelegramEntity, NewTelegramEntity } from '../telegram-entity.model';

export type PartialUpdateTelegramEntity = Partial<ITelegramEntity> & Pick<ITelegramEntity, 'id'>;

export type EntityResponseType = HttpResponse<ITelegramEntity>;
export type EntityArrayResponseType = HttpResponse<ITelegramEntity[]>;

@Injectable({ providedIn: 'root' })
export class TelegramEntityService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/telegram-entities');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(telegramEntity: NewTelegramEntity): Observable<EntityResponseType> {
    return this.http.post<ITelegramEntity>(this.resourceUrl, telegramEntity, { observe: 'response' });
  }

  update(telegramEntity: ITelegramEntity): Observable<EntityResponseType> {
    return this.http.put<ITelegramEntity>(`${this.resourceUrl}/${this.getTelegramEntityIdentifier(telegramEntity)}`, telegramEntity, {
      observe: 'response',
    });
  }

  partialUpdate(telegramEntity: PartialUpdateTelegramEntity): Observable<EntityResponseType> {
    return this.http.patch<ITelegramEntity>(`${this.resourceUrl}/${this.getTelegramEntityIdentifier(telegramEntity)}`, telegramEntity, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITelegramEntity>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITelegramEntity[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTelegramEntityIdentifier(telegramEntity: Pick<ITelegramEntity, 'id'>): number {
    return telegramEntity.id;
  }

  compareTelegramEntity(o1: Pick<ITelegramEntity, 'id'> | null, o2: Pick<ITelegramEntity, 'id'> | null): boolean {
    return o1 && o2 ? this.getTelegramEntityIdentifier(o1) === this.getTelegramEntityIdentifier(o2) : o1 === o2;
  }

  addTelegramEntityToCollectionIfMissing<Type extends Pick<ITelegramEntity, 'id'>>(
    telegramEntityCollection: Type[],
    ...telegramEntitiesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const telegramEntities: Type[] = telegramEntitiesToCheck.filter(isPresent);
    if (telegramEntities.length > 0) {
      const telegramEntityCollectionIdentifiers = telegramEntityCollection.map(
        telegramEntityItem => this.getTelegramEntityIdentifier(telegramEntityItem)!
      );
      const telegramEntitiesToAdd = telegramEntities.filter(telegramEntityItem => {
        const telegramEntityIdentifier = this.getTelegramEntityIdentifier(telegramEntityItem);
        if (telegramEntityCollectionIdentifiers.includes(telegramEntityIdentifier)) {
          return false;
        }
        telegramEntityCollectionIdentifiers.push(telegramEntityIdentifier);
        return true;
      });
      return [...telegramEntitiesToAdd, ...telegramEntityCollection];
    }
    return telegramEntityCollection;
  }
}
