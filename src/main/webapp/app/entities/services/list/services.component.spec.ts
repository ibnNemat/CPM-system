import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ServicesService } from '../service/services.service';

import { ServicesComponent } from './services.component';

describe('Services Management Component', () => {
  let comp: ServicesComponent;
  let fixture: ComponentFixture<ServicesComponent>;
  let service: ServicesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'services', component: ServicesComponent }]), HttpClientTestingModule],
      declarations: [ServicesComponent],
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
      .overrideTemplate(ServicesComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ServicesComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ServicesService);

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
    expect(comp.services?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to servicesService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getServicesIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getServicesIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
