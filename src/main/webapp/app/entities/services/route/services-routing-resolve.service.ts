import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IServices } from '../services.model';
import { ServicesService } from '../service/services.service';

@Injectable({ providedIn: 'root' })
export class ServicesRoutingResolveService implements Resolve<IServices | null> {
  constructor(protected service: ServicesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IServices | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((services: HttpResponse<IServices>) => {
          if (services.body) {
            return of(services.body);
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
