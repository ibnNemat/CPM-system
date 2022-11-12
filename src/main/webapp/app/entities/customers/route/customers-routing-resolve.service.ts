import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICustomers } from '../customers.model';
import { CustomersService } from '../service/customers.service';

@Injectable({ providedIn: 'root' })
export class CustomersRoutingResolveService implements Resolve<ICustomers | null> {
  constructor(protected service: CustomersService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICustomers | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((customers: HttpResponse<ICustomers>) => {
          if (customers.body) {
            return of(customers.body);
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
