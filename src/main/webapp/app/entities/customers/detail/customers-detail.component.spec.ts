import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CustomersDetailComponent } from './customers-detail.component';

describe('Customers Management Detail Component', () => {
  let comp: CustomersDetailComponent;
  let fixture: ComponentFixture<CustomersDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CustomersDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ customers: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CustomersDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CustomersDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load customers on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.customers).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
