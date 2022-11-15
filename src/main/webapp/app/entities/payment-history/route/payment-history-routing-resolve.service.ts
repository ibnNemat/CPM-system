import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPaymentHistory } from '../payment-history.model';
import { PaymentHistoryService } from '../service/payment-history.service';

@Injectable({ providedIn: 'root' })
export class PaymentHistoryRoutingResolveService implements Resolve<IPaymentHistory | null> {
  constructor(protected service: PaymentHistoryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPaymentHistory | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((paymentHistory: HttpResponse<IPaymentHistory>) => {
          if (paymentHistory.body) {
            return of(paymentHistory.body);
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
