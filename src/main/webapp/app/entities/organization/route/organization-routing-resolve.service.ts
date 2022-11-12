import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrganization } from '../organization.model';
import { OrganizationService } from '../service/organization.service';

@Injectable({ providedIn: 'root' })
export class OrganizationRoutingResolveService implements Resolve<IOrganization | null> {
  constructor(protected service: OrganizationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOrganization | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((organization: HttpResponse<IOrganization>) => {
          if (organization.body) {
            return of(organization.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
