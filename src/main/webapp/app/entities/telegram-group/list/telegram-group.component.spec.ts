import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TelegramGroupService } from '../service/telegram-group.service';

import { TelegramGroupComponent } from './telegram-group.component';

describe('TelegramGroup Management Component', () => {
  let comp: TelegramGroupComponent;
  let fixture: ComponentFixture<TelegramGroupComponent>;
  let service: TelegramGroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'telegram-group', component: TelegramGroupComponent }]), HttpClientTestingModule],
      declarations: [TelegramGroupComponent],
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
      .overrideTemplate(TelegramGroupComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TelegramGroupComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TelegramGroupService);

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
    expect(comp.telegramGroups?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to telegramGroupService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getTelegramGroupIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getTelegramGroupIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
