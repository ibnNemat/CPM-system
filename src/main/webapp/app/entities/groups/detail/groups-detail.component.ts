import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGroups } from '../groups.model';

@Component({
  selector: 'jhi-groups-detail',
  templateUrl: './groups-detail.component.html',
})
export class GroupsDetailComponent implements OnInit {
  groups: IGroups | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ groups }) => {
      this.groups = groups;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
