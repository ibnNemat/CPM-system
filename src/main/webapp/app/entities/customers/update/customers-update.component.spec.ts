import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CustomersFormService } from './customers-form.service';
import { CustomersService } from '../service/customers.service';
import { ICustomers } from '../customers.model';
import { IGroups } from 'app/entities/groups/groups.model';
import { GroupsService } from 'app/entities/groups/service/groups.service';
import { IServices } from 'app/entities/services/services.model';
import { ServicesService } from 'app/entities/services/service/services.service';

import { CustomersUpdateComponent } from './customers-update.component';

describe('Customers Management Update Component', () => {
  let comp: CustomersUpdateComponent;
  let fixture: ComponentFixture<CustomersUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let customersFormService: CustomersFormService;
  let customersService: CustomersService;
  let groupsService: GroupsService;
  let servicesService: ServicesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CustomersUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CustomersUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CustomersUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    customersFormService = TestBed.inject(CustomersFormService);
    customersService = TestBed.inject(CustomersService);
    groupsService = TestBed.inject(GroupsService);
    servicesService = TestBed.inject(ServicesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Groups query and add missing value', () => {
      const customers: ICustomers = { id: 456 };
      const groups: IGroups[] = [{ id: 27496 }];
      customers.groups = groups;

      const groupsCollection: IGroups[] = [{ id: 94353 }];
      jest.spyOn(groupsService, 'query').mockReturnValue(of(new HttpResponse({ body: groupsCollection })));
      const additionalGroups = [...groups];
      const expectedCollection: IGroups[] = [...additionalGroups, ...groupsCollection];
      jest.spyOn(groupsService, 'addGroupsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ customers });
      comp.ngOnInit();

      expect(groupsService.query).toHaveBeenCalled();
      expect(groupsService.addGroupsToCollectionIfMissing).toHaveBeenCalledWith(
        groupsCollection,
        ...additionalGroups.map(expect.objectContaining)
      );
      expect(comp.groupsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Services query and add missing value', () => {
      const customers: ICustomers = { id: 456 };
      const services: IServices[] = [{ id: 39275 }];
      customers.services = services;

      const servicesCollection: IServices[] = [{ id: 14973 }];
      jest.spyOn(servicesService, 'query').mockReturnValue(of(new HttpResponse({ body: servicesCollection })));
      const additionalServices = [...services];
      const expectedCollection: IServices[] = [...additionalServices, ...servicesCollection];
      jest.spyOn(servicesService, 'addServicesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ customers });
      comp.ngOnInit();

      expect(servicesService.query).toHaveBeenCalled();
      expect(servicesService.addServicesToCollectionIfMissing).toHaveBeenCalledWith(
        servicesCollection,
        ...additionalServices.map(expect.objectContaining)
      );
      expect(comp.servicesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const customers: ICustomers = { id: 456 };
      const groups: IGroups = { id: 17548 };
      customers.groups = [groups];
      const services: IServices = { id: 65202 };
      customers.services = [services];

      activatedRoute.data = of({ customers });
      comp.ngOnInit();

      expect(comp.groupsSharedCollection).toContain(groups);
      expect(comp.servicesSharedCollection).toContain(services);
      expect(comp.customers).toEqual(customers);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICustomers>>();
      const customers = { id: 123 };
      jest.spyOn(customersFormService, 'getCustomers').mockReturnValue(customers);
      jest.spyOn(customersService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customers });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: customers }));
      saveSubject.complete();

      // THEN
      expect(customersFormService.getCustomers).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(customersService.update).toHaveBeenCalledWith(expect.objectContaining(customers));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICustomers>>();
      const customers = { id: 123 };
      jest.spyOn(customersFormService, 'getCustomers').mockReturnValue({ id: null });
      jest.spyOn(customersService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customers: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: customers }));
      saveSubject.complete();

      // THEN
      expect(customersFormService.getCustomers).toHaveBeenCalled();
      expect(customersService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICustomers>>();
      const customers = { id: 123 };
      jest.spyOn(customersService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customers });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(customersService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareGroups', () => {
      it('Should forward to groupsService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(groupsService, 'compareGroups');
        comp.compareGroups(entity, entity2);
        expect(groupsService.compareGroups).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareServices', () => {
      it('Should forward to servicesService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(servicesService, 'compareServices');
        comp.compareServices(entity, entity2);
        expect(servicesService.compareServices).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
