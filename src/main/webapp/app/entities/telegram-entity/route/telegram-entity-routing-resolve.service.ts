import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITelegramEntity } from '../telegram-entity.model';
import { TelegramEntityService } from '../service/telegram-entity.service';

@Injectable({ providedIn: 'root' })
export class TelegramEntityRoutingResolveService implements Resolve<ITelegramEntity | null> {
  constructor(protected service: TelegramEntityService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITelegramEntity | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((telegramEntity: HttpResponse<ITelegramEntity>) => {
          if (telegramEntity.body) {
            return of(telegramEntity.body);
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
