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

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { CustomersUpdateComponent } from './customers-update.component';

describe('Customers Management Update Component', () => {
  let comp: CustomersUpdateComponent;
  let fixture: ComponentFixture<CustomersUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let customersFormService: CustomersFormService;
  let customersService: CustomersService;
  let userService: UserService;

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
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const customers: ICustomers = { id: 456 };
      const user: IUser = { id: 15589 };
      customers.user = user;

      const userCollection: IUser[] = [{ id: 17029 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ customers });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const customers: ICustomers = { id: 456 };
      const user: IUser = { id: 64561 };
      customers.user = user;

      activatedRoute.data = of({ customers });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
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
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
