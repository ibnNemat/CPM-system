import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { BotTokenService } from '../service/bot-token.service';

import { BotTokenComponent } from './bot-token.component';

describe('BotToken Management Component', () => {
  let comp: BotTokenComponent;
  let fixture: ComponentFixture<BotTokenComponent>;
  let service: BotTokenService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'bot-token', component: BotTokenComponent }]), HttpClientTestingModule],
      declarations: [BotTokenComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(BotTokenComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BotTokenComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(BotTokenService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.botTokens?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to botTokenService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getBotTokenIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getBotTokenIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
