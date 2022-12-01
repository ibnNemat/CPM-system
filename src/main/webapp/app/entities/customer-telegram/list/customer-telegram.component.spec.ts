import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CustomerTelegramService } from '../service/customer-telegram.service';

import { CustomerTelegramComponent } from './customer-telegram.component';

describe('CustomerTelegram Management Component', () => {
  let comp: CustomerTelegramComponent;
  let fixture: ComponentFixture<CustomerTelegramComponent>;
  let service: CustomerTelegramService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'customer-telegram', component: CustomerTelegramComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [CustomerTelegramComponent],
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
      .overrideTemplate(CustomerTelegramComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CustomerTelegramComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CustomerTelegramService);

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
    expect(comp.customerTelegrams?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to customerTelegramService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getCustomerTelegramIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getCustomerTelegramIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
