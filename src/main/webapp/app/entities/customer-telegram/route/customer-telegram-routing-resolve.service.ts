import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICustomerTelegram } from '../customer-telegram.model';
import { CustomerTelegramService } from '../service/customer-telegram.service';

@Injectable({ providedIn: 'root' })
export class CustomerTelegramRoutingResolveService implements Resolve<ICustomerTelegram | null> {
  constructor(protected service: CustomerTelegramService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICustomerTelegram | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((customerTelegram: HttpResponse<ICustomerTelegram>) => {
          if (customerTelegram.body) {
            return of(customerTelegram.body);
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
