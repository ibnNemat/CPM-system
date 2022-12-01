import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CustomerTelegramFormService } from './customer-telegram-form.service';
import { CustomerTelegramService } from '../service/customer-telegram.service';
import { ICustomerTelegram } from '../customer-telegram.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { CustomerTelegramUpdateComponent } from './customer-telegram-update.component';

describe('CustomerTelegram Management Update Component', () => {
  let comp: CustomerTelegramUpdateComponent;
  let fixture: ComponentFixture<CustomerTelegramUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let customerTelegramFormService: CustomerTelegramFormService;
  let customerTelegramService: CustomerTelegramService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CustomerTelegramUpdateComponent],
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
      .overrideTemplate(CustomerTelegramUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CustomerTelegramUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    customerTelegramFormService = TestBed.inject(CustomerTelegramFormService);
    customerTelegramService = TestBed.inject(CustomerTelegramService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const customerTelegram: ICustomerTelegram = { id: 456 };
      const user: IUser = { id: 74797 };
      customerTelegram.user = user;

      const userCollection: IUser[] = [{ id: 13928 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ customerTelegram });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const customerTelegram: ICustomerTelegram = { id: 456 };
      const user: IUser = { id: 25273 };
      customerTelegram.user = user;

      activatedRoute.data = of({ customerTelegram });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.customerTelegram).toEqual(customerTelegram);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICustomerTelegram>>();
      const customerTelegram = { id: 123 };
      jest.spyOn(customerTelegramFormService, 'getCustomerTelegram').mockReturnValue(customerTelegram);
      jest.spyOn(customerTelegramService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customerTelegram });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: customerTelegram }));
      saveSubject.complete();

      // THEN
      expect(customerTelegramFormService.getCustomerTelegram).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(customerTelegramService.update).toHaveBeenCalledWith(expect.objectContaining(customerTelegram));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICustomerTelegram>>();
      const customerTelegram = { id: 123 };
      jest.spyOn(customerTelegramFormService, 'getCustomerTelegram').mockReturnValue({ id: null });
      jest.spyOn(customerTelegramService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customerTelegram: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: customerTelegram }));
      saveSubject.complete();

      // THEN
      expect(customerTelegramFormService.getCustomerTelegram).toHaveBeenCalled();
      expect(customerTelegramService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICustomerTelegram>>();
      const customerTelegram = { id: 123 };
      jest.spyOn(customerTelegramService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customerTelegram });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(customerTelegramService.update).toHaveBeenCalled();
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
