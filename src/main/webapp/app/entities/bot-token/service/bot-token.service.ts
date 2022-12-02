import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBotToken, NewBotToken } from '../bot-token.model';

export type PartialUpdateBotToken = Partial<IBotToken> & Pick<IBotToken, 'id'>;

export type EntityResponseType = HttpResponse<IBotToken>;
export type EntityArrayResponseType = HttpResponse<IBotToken[]>;

@Injectable({ providedIn: 'root' })
export class BotTokenService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/bot-tokens');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(botToken: NewBotToken): Observable<EntityResponseType> {
    return this.http.post<IBotToken>(this.resourceUrl, botToken, { observe: 'response' });
  }

  update(botToken: IBotToken): Observable<EntityResponseType> {
    return this.http.put<IBotToken>(`${this.resourceUrl}/${this.getBotTokenIdentifier(botToken)}`, botToken, { observe: 'response' });
  }

  partialUpdate(botToken: PartialUpdateBotToken): Observable<EntityResponseType> {
    return this.http.patch<IBotToken>(`${this.resourceUrl}/${this.getBotTokenIdentifier(botToken)}`, botToken, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBotToken>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBotToken[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getBotTokenIdentifier(botToken: Pick<IBotToken, 'id'>): number {
    return botToken.id;
  }

  compareBotToken(o1: Pick<IBotToken, 'id'> | null, o2: Pick<IBotToken, 'id'> | null): boolean {
    return o1 && o2 ? this.getBotTokenIdentifier(o1) === this.getBotTokenIdentifier(o2) : o1 === o2;
  }

  addBotTokenToCollectionIfMissing<Type extends Pick<IBotToken, 'id'>>(
    botTokenCollection: Type[],
    ...botTokensToCheck: (Type | null | undefined)[]
  ): Type[] {
    const botTokens: Type[] = botTokensToCheck.filter(isPresent);
    if (botTokens.length > 0) {
      const botTokenCollectionIdentifiers = botTokenCollection.map(botTokenItem => this.getBotTokenIdentifier(botTokenItem)!);
      const botTokensToAdd = botTokens.filter(botTokenItem => {
        const botTokenIdentifier = this.getBotTokenIdentifier(botTokenItem);
        if (botTokenCollectionIdentifiers.includes(botTokenIdentifier)) {
          return false;
        }
        botTokenCollectionIdentifiers.push(botTokenIdentifier);
        return true;
      });
      return [...botTokensToAdd, ...botTokenCollection];
    }
    return botTokenCollection;
  }
}
