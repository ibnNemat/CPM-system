import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICustomers, NewCustomers } from '../customers.model';

export type PartialUpdateCustomers = Partial<ICustomers> & Pick<ICustomers, 'id'>;

export type EntityResponseType = HttpResponse<ICustomers>;
export type EntityArrayResponseType = HttpResponse<ICustomers[]>;

@Injectable({ providedIn: 'root' })
export class CustomersService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/customers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(customers: NewCustomers): Observable<EntityResponseType> {
    return this.http.post<ICustomers>(this.resourceUrl, customers, { observe: 'response' });
  }

  update(customers: ICustomers): Observable<EntityResponseType> {
    return this.http.put<ICustomers>(`${this.resourceUrl}/${this.getCustomersIdentifier(customers)}`, customers, { observe: 'response' });
  }

  partialUpdate(customers: PartialUpdateCustomers): Observable<EntityResponseType> {
    return this.http.patch<ICustomers>(`${this.resourceUrl}/${this.getCustomersIdentifier(customers)}`, customers, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICustomers>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICustomers[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getCustomersIdentifier(customers: Pick<ICustomers, 'id'>): number {
    return customers.id;
  }

  compareCustomers(o1: Pick<ICustomers, 'id'> | null, o2: Pick<ICustomers, 'id'> | null): boolean {
    return o1 && o2 ? this.getCustomersIdentifier(o1) === this.getCustomersIdentifier(o2) : o1 === o2;
  }

  addCustomersToCollectionIfMissing<Type extends Pick<ICustomers, 'id'>>(
    customersCollection: Type[],
    ...customersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const customers: Type[] = customersToCheck.filter(isPresent);
    if (customers.length > 0) {
      const customersCollectionIdentifiers = customersCollection.map(customersItem => this.getCustomersIdentifier(customersItem)!);
      const customersToAdd = customers.filter(customersItem => {
        const customersIdentifier = this.getCustomersIdentifier(customersItem);
        if (customersCollectionIdentifiers.includes(customersIdentifier)) {
          return false;
        }
        customersCollectionIdentifiers.push(customersIdentifier);
        return true;
      });
      return [...customersToAdd, ...customersCollection];
    }
    return customersCollection;
  }
}
