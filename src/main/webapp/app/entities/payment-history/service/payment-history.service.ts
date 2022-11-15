import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPaymentHistory, NewPaymentHistory } from '../payment-history.model';

export type PartialUpdatePaymentHistory = Partial<IPaymentHistory> & Pick<IPaymentHistory, 'id'>;

type RestOf<T extends IPaymentHistory | NewPaymentHistory> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestPaymentHistory = RestOf<IPaymentHistory>;

export type NewRestPaymentHistory = RestOf<NewPaymentHistory>;

export type PartialUpdateRestPaymentHistory = RestOf<PartialUpdatePaymentHistory>;

export type EntityResponseType = HttpResponse<IPaymentHistory>;
export type EntityArrayResponseType = HttpResponse<IPaymentHistory[]>;

@Injectable({ providedIn: 'root' })
export class PaymentHistoryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/payment-histories');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(paymentHistory: NewPaymentHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(paymentHistory);
    return this.http
      .post<RestPaymentHistory>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(paymentHistory: IPaymentHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(paymentHistory);
    return this.http
      .put<RestPaymentHistory>(`${this.resourceUrl}/${this.getPaymentHistoryIdentifier(paymentHistory)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(paymentHistory: PartialUpdatePaymentHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(paymentHistory);
    return this.http
      .patch<RestPaymentHistory>(`${this.resourceUrl}/${this.getPaymentHistoryIdentifier(paymentHistory)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPaymentHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPaymentHistory[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPaymentHistoryIdentifier(paymentHistory: Pick<IPaymentHistory, 'id'>): number {
    return paymentHistory.id;
  }

  comparePaymentHistory(o1: Pick<IPaymentHistory, 'id'> | null, o2: Pick<IPaymentHistory, 'id'> | null): boolean {
    return o1 && o2 ? this.getPaymentHistoryIdentifier(o1) === this.getPaymentHistoryIdentifier(o2) : o1 === o2;
  }

  addPaymentHistoryToCollectionIfMissing<Type extends Pick<IPaymentHistory, 'id'>>(
    paymentHistoryCollection: Type[],
    ...paymentHistoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const paymentHistories: Type[] = paymentHistoriesToCheck.filter(isPresent);
    if (paymentHistories.length > 0) {
      const paymentHistoryCollectionIdentifiers = paymentHistoryCollection.map(
        paymentHistoryItem => this.getPaymentHistoryIdentifier(paymentHistoryItem)!
      );
      const paymentHistoriesToAdd = paymentHistories.filter(paymentHistoryItem => {
        const paymentHistoryIdentifier = this.getPaymentHistoryIdentifier(paymentHistoryItem);
        if (paymentHistoryCollectionIdentifiers.includes(paymentHistoryIdentifier)) {
          return false;
        }
        paymentHistoryCollectionIdentifiers.push(paymentHistoryIdentifier);
        return true;
      });
      return [...paymentHistoriesToAdd, ...paymentHistoryCollection];
    }
    return paymentHistoryCollection;
  }

  protected convertDateFromClient<T extends IPaymentHistory | NewPaymentHistory | PartialUpdatePaymentHistory>(
    paymentHistory: T
  ): RestOf<T> {
    return {
      ...paymentHistory,
      createdAt: paymentHistory.createdAt?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restPaymentHistory: RestPaymentHistory): IPaymentHistory {
    return {
      ...restPaymentHistory,
      createdAt: restPaymentHistory.createdAt ? dayjs(restPaymentHistory.createdAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPaymentHistory>): HttpResponse<IPaymentHistory> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPaymentHistory[]>): HttpResponse<IPaymentHistory[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
