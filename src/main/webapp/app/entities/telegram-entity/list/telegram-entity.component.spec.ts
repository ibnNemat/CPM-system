import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TelegramEntityService } from '../service/telegram-entity.service';

import { TelegramEntityComponent } from './telegram-entity.component';

describe('TelegramEntity Management Component', () => {
  let comp: TelegramEntityComponent;
  let fixture: ComponentFixture<TelegramEntityComponent>;
  let service: TelegramEntityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'telegram-entity', component: TelegramEntityComponent }]), HttpClientTestingModule],
      declarations: [TelegramEntityComponent],
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
      .overrideTemplate(TelegramEntityComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TelegramEntityComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TelegramEntityService);

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
    expect(comp.telegramEntities?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to telegramEntityService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getTelegramEntityIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getTelegramEntityIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
