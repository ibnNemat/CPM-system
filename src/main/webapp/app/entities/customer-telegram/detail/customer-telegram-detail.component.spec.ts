import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CustomerTelegramDetailComponent } from './customer-telegram-detail.component';

describe('CustomerTelegram Management Detail Component', () => {
  let comp: CustomerTelegramDetailComponent;
  let fixture: ComponentFixture<CustomerTelegramDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CustomerTelegramDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ customerTelegram: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CustomerTelegramDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CustomerTelegramDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load customerTelegram on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.customerTelegram).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
