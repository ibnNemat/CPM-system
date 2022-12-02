import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BotTokenDetailComponent } from './bot-token-detail.component';

describe('BotToken Management Detail Component', () => {
  let comp: BotTokenDetailComponent;
  let fixture: ComponentFixture<BotTokenDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BotTokenDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ botToken: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(BotTokenDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BotTokenDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load botToken on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.botToken).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
