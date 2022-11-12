import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ServicesDetailComponent } from './services-detail.component';

describe('Services Management Detail Component', () => {
  let comp: ServicesDetailComponent;
  let fixture: ComponentFixture<ServicesDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ServicesDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ services: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ServicesDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ServicesDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load services on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.services).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
