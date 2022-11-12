import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IServices, NewServices } from '../services.model';

export type PartialUpdateServices = Partial<IServices> & Pick<IServices, 'id'>;

export type EntityResponseType = HttpResponse<IServices>;
export type EntityArrayResponseType = HttpResponse<IServices[]>;

@Injectable({ providedIn: 'root' })
export class ServicesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/services');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(services: NewServices): Observable<EntityResponseType> {
    return this.http.post<IServices>(this.resourceUrl, services, { observe: 'response' });
  }

  update(services: IServices): Observable<EntityResponseType> {
    return this.http.put<IServices>(`${this.resourceUrl}/${this.getServicesIdentifier(services)}`, services, { observe: 'response' });
  }

  partialUpdate(services: PartialUpdateServices): Observable<EntityResponseType> {
    return this.http.patch<IServices>(`${this.resourceUrl}/${this.getServicesIdentifier(services)}`, services, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IServices>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IServices[]>(this.resourceUrl, { params: options, observe: 'response' });
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
}
