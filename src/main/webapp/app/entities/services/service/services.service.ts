import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IServices, NewServices } from '../services.model';

export type PartialUpdateServices = Partial<IServices> & Pick<IServices, 'id'>;

type RestOf<T extends IServices | NewServices> = Omit<T, 'startedPeriod'> & {
  startedPeriod?: string | null;
};

export type RestServices = RestOf<IServices>;

export type NewRestServices = RestOf<NewServices>;

export type PartialUpdateRestServices = RestOf<PartialUpdateServices>;

export type EntityResponseType = HttpResponse<IServices>;
export type EntityArrayResponseType = HttpResponse<IServices[]>;

@Injectable({ providedIn: 'root' })
export class ServicesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/services');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(services: NewServices): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(services);
    return this.http
      .post<RestServices>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(services: IServices): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(services);
    return this.http
      .put<RestServices>(`${this.resourceUrl}/${this.getServicesIdentifier(services)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(services: PartialUpdateServices): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(services);
    return this.http
      .patch<RestServices>(`${this.resourceUrl}/${this.getServicesIdentifier(services)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestServices>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestServices[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getServicesIdentifier(services: Pick<IServices, 'id'>): number {
    return services.id;
  }

  compareServices(o1: Pick<IServices, 'id'> | null, o2: Pick<IServices, 'id'> | null): boolean {
    return o1 && o2 ? this.getServicesIdentifier(o1) === this.getServicesIdentifier(o2) : o1 === o2;
  }

  addServicesToCollectionIfMissing<Type extends Pick<IServices, 'id'>>(
    servicesCollection: Type[],
    ...servicesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const services: Type[] = servicesToCheck.filter(isPresent);
    if (services.length > 0) {
      const servicesCollectionIdentifiers = servicesCollection.map(servicesItem => this.getServicesIdentifier(servicesItem)!);
      const servicesToAdd = services.filter(servicesItem => {
        const servicesIdentifier = this.getServicesIdentifier(servicesItem);
        if (servicesCollectionIdentifiers.includes(servicesIdentifier)) {
          return false;
        }
        servicesCollectionIdentifiers.push(servicesIdentifier);
        return true;
      });
      return [...servicesToAdd, ...servicesCollection];
    }
    return servicesCollection;
  }

  protected convertDateFromClient<T extends IServices | NewServices | PartialUpdateServices>(services: T): RestOf<T> {
    return {
      ...services,
      startedPeriod: services.startedPeriod?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restServices: RestServices): IServices {
    return {
      ...restServices,
      startedPeriod: restServices.startedPeriod ? dayjs(restServices.startedPeriod) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestServices>): HttpResponse<IServices> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestServices[]>): HttpResponse<IServices[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
