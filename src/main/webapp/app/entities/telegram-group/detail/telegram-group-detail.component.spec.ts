import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TelegramGroupDetailComponent } from './telegram-group-detail.component';

describe('TelegramGroup Management Detail Component', () => {
  let comp: TelegramGroupDetailComponent;
  let fixture: ComponentFixture<TelegramGroupDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TelegramGroupDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ telegramGroup: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TelegramGroupDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TelegramGroupDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load telegramGroup on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.telegramGroup).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
