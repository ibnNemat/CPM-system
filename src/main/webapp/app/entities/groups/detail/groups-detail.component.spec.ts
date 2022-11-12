import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GroupsDetailComponent } from './groups-detail.component';

describe('Groups Management Detail Component', () => {
  let comp: GroupsDetailComponent;
  let fixture: ComponentFixture<GroupsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GroupsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ groups: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(GroupsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(GroupsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load groups on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.groups).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
