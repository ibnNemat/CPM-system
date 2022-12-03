import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITelegramGroup } from '../telegram-group.model';
import { TelegramGroupService } from '../service/telegram-group.service';

@Injectable({ providedIn: 'root' })
export class TelegramGroupRoutingResolveService implements Resolve<ITelegramGroup | null> {
  constructor(protected service: TelegramGroupService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITelegramGroup | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((telegramGroup: HttpResponse<ITelegramGroup>) => {
          if (telegramGroup.body) {
            return of(telegramGroup.body);
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
