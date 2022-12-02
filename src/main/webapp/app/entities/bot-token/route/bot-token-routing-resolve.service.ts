import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBotToken } from '../bot-token.model';
import { BotTokenService } from '../service/bot-token.service';

@Injectable({ providedIn: 'root' })
export class BotTokenRoutingResolveService implements Resolve<IBotToken | null> {
  constructor(protected service: BotTokenService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBotToken | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((botToken: HttpResponse<IBotToken>) => {
          if (botToken.body) {
            return of(botToken.body);
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
