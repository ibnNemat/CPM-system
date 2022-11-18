import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ServicesFormService } from './services-form.service';
import { ServicesService } from '../service/services.service';
import { IServices } from '../services.model';
import { IGroups } from 'app/entities/groups/groups.model';
import { GroupsService } from 'app/entities/groups/service/groups.service';
import { ICustomers } from 'app/entities/customers/customers.model';
import { CustomersService } from 'app/entities/customers/service/customers.service';

import { ServicesUpdateComponent } from './services-update.component';

describe('Services Management Update Component', () => {
  let comp: ServicesUpdateComponent;
  let fixture: ComponentFixture<ServicesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let servicesFormService: ServicesFormService;
  let servicesService: ServicesService;
  let groupsService: GroupsService;
  let customersService: CustomersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ServicesUpdateComponent],
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
      .overrideTemplate(ServicesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ServicesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    servicesFormService = TestBed.inject(ServicesFormService);
    servicesService = TestBed.inject(ServicesService);
    groupsService = TestBed.inject(GroupsService);
    customersService = TestBed.inject(CustomersService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Groups query and add missing value', () => {
      const services: IServices = { id: 456 };
      const group: IGroups = { id: 7809 };
      services.group = group;

      const groupsCollection: IGroups[] = [{ id: 67615 }];
      jest.spyOn(groupsService, 'query').mockReturnValue(of(new HttpResponse({ body: groupsCollection })));
      const additionalGroups = [group];
      const expectedCollection: IGroups[] = [...additionalGroups, ...groupsCollection];
      jest.spyOn(groupsService, 'addGroupsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ services });
      comp.ngOnInit();

      expect(groupsService.query).toHaveBeenCalled();
      expect(groupsService.addGroupsToCollectionIfMissing).toHaveBeenCalledWith(
        groupsCollection,
        ...additionalGroups.map(expect.objectContaining)
      );
      expect(comp.groupsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Customers query and add missing value', () => {
      const services: IServices = { id: 456 };
      const users: ICustomers[] = [{ id: 72914 }];
      services.users = users;

      const customersCollection: ICustomers[] = [{ id: 19762 }];
      jest.spyOn(customersService, 'query').mockReturnValue(of(new HttpResponse({ body: customersCollection })));
      const additionalCustomers = [...users];
      const expectedCollection: ICustomers[] = [...additionalCustomers, ...customersCollection];
      jest.spyOn(customersService, 'addCustomersToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ services });
      comp.ngOnInit();

      expect(customersService.query).toHaveBeenCalled();
      expect(customersService.addCustomersToCollectionIfMissing).toHaveBeenCalledWith(
        customersCollection,
        ...additionalCustomers.map(expect.objectContaining)
      );
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const services: IServices = { id: 456 };
      const group: IGroups = { id: 97153 };
      services.group = group;
      const users: ICustomers = { id: 42151 };
      services.users = [users];

      activatedRoute.data = of({ services });
      comp.ngOnInit();

      expect(comp.groupsSharedCollection).toContain(group);
      expect(comp.customersSharedCollection).toContain(users);
      expect(comp.services).toEqual(services);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IServices>>();
      const services = { id: 123 };
      jest.spyOn(servicesFormService, 'getServices').mockReturnValue(services);
      jest.spyOn(servicesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ services });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: services }));
      saveSubject.complete();

      // THEN
      expect(servicesFormService.getServices).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(servicesService.update).toHaveBeenCalledWith(expect.objectContaining(services));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IServices>>();
      const services = { id: 123 };
      jest.spyOn(servicesFormService, 'getServices').mockReturnValue({ id: null });
      jest.spyOn(servicesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ services: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: services }));
      saveSubject.complete();

      // THEN
      expect(servicesFormService.getServices).toHaveBeenCalled();
      expect(servicesService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IServices>>();
      const services = { id: 123 };
      jest.spyOn(servicesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ services });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(servicesService.update).toHaveBeenCalled();
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

    describe('compareCustomers', () => {
      it('Should forward to customersService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(customersService, 'compareCustomers');
        comp.compareCustomers(entity, entity2);
        expect(customersService.compareCustomers).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
