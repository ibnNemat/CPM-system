import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TelegramEntityDetailComponent } from './telegram-entity-detail.component';

describe('TelegramEntity Management Detail Component', () => {
  let comp: TelegramEntityDetailComponent;
  let fixture: ComponentFixture<TelegramEntityDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TelegramEntityDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ telegramEntity: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TelegramEntityDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TelegramEntityDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load telegramEntity on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.telegramEntity).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
