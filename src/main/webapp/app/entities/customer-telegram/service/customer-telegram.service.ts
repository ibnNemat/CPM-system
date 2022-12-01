import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICustomerTelegram, NewCustomerTelegram } from '../customer-telegram.model';

export type PartialUpdateCustomerTelegram = Partial<ICustomerTelegram> & Pick<ICustomerTelegram, 'id'>;

export type EntityResponseType = HttpResponse<ICustomerTelegram>;
export type EntityArrayResponseType = HttpResponse<ICustomerTelegram[]>;

@Injectable({ providedIn: 'root' })
export class CustomerTelegramService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/customer-telegrams');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(customerTelegram: NewCustomerTelegram): Observable<EntityResponseType> {
    return this.http.post<ICustomerTelegram>(this.resourceUrl, customerTelegram, { observe: 'response' });
  }

  update(customerTelegram: ICustomerTelegram): Observable<EntityResponseType> {
    return this.http.put<ICustomerTelegram>(
      `${this.resourceUrl}/${this.getCustomerTelegramIdentifier(customerTelegram)}`,
      customerTelegram,
      { observe: 'response' }
    );
  }

  partialUpdate(customerTelegram: PartialUpdateCustomerTelegram): Observable<EntityResponseType> {
    return this.http.patch<ICustomerTelegram>(
      `${this.resourceUrl}/${this.getCustomerTelegramIdentifier(customerTelegram)}`,
      customerTelegram,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICustomerTelegram>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICustomerTelegram[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCustomerTelegramIdentifier(customerTelegram: Pick<ICustomerTelegram, 'id'>): number {
    return customerTelegram.id;
  }

  compareCustomerTelegram(o1: Pick<ICustomerTelegram, 'id'> | null, o2: Pick<ICustomerTelegram, 'id'> | null): boolean {
    return o1 && o2 ? this.getCustomerTelegramIdentifier(o1) === this.getCustomerTelegramIdentifier(o2) : o1 === o2;
  }

  addCustomerTelegramToCollectionIfMissing<Type extends Pick<ICustomerTelegram, 'id'>>(
    customerTelegramCollection: Type[],
    ...customerTelegramsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const customerTelegrams: Type[] = customerTelegramsToCheck.filter(isPresent);
    if (customerTelegrams.length > 0) {
      const customerTelegramCollectionIdentifiers = customerTelegramCollection.map(
        customerTelegramItem => this.getCustomerTelegramIdentifier(customerTelegramItem)!
      );
      const customerTelegramsToAdd = customerTelegrams.filter(customerTelegramItem => {
        const customerTelegramIdentifier = this.getCustomerTelegramIdentifier(customerTelegramItem);
        if (customerTelegramCollectionIdentifiers.includes(customerTelegramIdentifier)) {
          return false;
        }
        customerTelegramCollectionIdentifiers.push(customerTelegramIdentifier);
        return true;
      });
      return [...customerTelegramsToAdd, ...customerTelegramCollection];
    }
    return customerTelegramCollection;
  }
}
