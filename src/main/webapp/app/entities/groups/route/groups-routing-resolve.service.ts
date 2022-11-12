import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGroups } from '../groups.model';
import { GroupsService } from '../service/groups.service';

@Injectable({ providedIn: 'root' })
export class GroupsRoutingResolveService implements Resolve<IGroups | null> {
  constructor(protected service: GroupsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGroups | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((groups: HttpResponse<IGroups>) => {
          if (groups.body) {
            return of(groups.body);
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
