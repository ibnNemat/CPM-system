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
import { ICustomers } from 'app/entities/customers/customers.model';
import { CustomersService } from 'app/entities/customers/service/customers.service';
import { ITelegramGroup } from 'app/entities/telegram-group/telegram-group.model';
import { TelegramGroupService } from 'app/entities/telegram-group/service/telegram-group.service';

import { CustomerTelegramUpdateComponent } from './customer-telegram-update.component';

describe('CustomerTelegram Management Update Component', () => {
  let comp: CustomerTelegramUpdateComponent;
  let fixture: ComponentFixture<CustomerTelegramUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let customerTelegramFormService: CustomerTelegramFormService;
  let customerTelegramService: CustomerTelegramService;
  let customersService: CustomersService;
  let telegramGroupService: TelegramGroupService;

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
    customersService = TestBed.inject(CustomersService);
    telegramGroupService = TestBed.inject(TelegramGroupService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Customers query and add missing value', () => {
      const customerTelegram: ICustomerTelegram = { id: 456 };
      const customer: ICustomers = { id: 29894 };
      customerTelegram.customer = customer;

      const customersCollection: ICustomers[] = [{ id: 47903 }];
      jest.spyOn(customersService, 'query').mockReturnValue(of(new HttpResponse({ body: customersCollection })));
      const additionalCustomers = [customer];
      const expectedCollection: ICustomers[] = [...additionalCustomers, ...customersCollection];
      jest.spyOn(customersService, 'addCustomersToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ customerTelegram });
      comp.ngOnInit();

      expect(customersService.query).toHaveBeenCalled();
      expect(customersService.addCustomersToCollectionIfMissing).toHaveBeenCalledWith(
        customersCollection,
        ...additionalCustomers.map(expect.objectContaining)
      );
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call TelegramGroup query and add missing value', () => {
      const customerTelegram: ICustomerTelegram = { id: 456 };
      const telegramGroups: ITelegramGroup[] = [{ id: 42233 }];
      customerTelegram.telegramGroups = telegramGroups;

      const telegramGroupCollection: ITelegramGroup[] = [{ id: 23805 }];
      jest.spyOn(telegramGroupService, 'query').mockReturnValue(of(new HttpResponse({ body: telegramGroupCollection })));
      const additionalTelegramGroups = [...telegramGroups];
      const expectedCollection: ITelegramGroup[] = [...additionalTelegramGroups, ...telegramGroupCollection];
      jest.spyOn(telegramGroupService, 'addTelegramGroupToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ customerTelegram });
      comp.ngOnInit();

      expect(telegramGroupService.query).toHaveBeenCalled();
      expect(telegramGroupService.addTelegramGroupToCollectionIfMissing).toHaveBeenCalledWith(
        telegramGroupCollection,
        ...additionalTelegramGroups.map(expect.objectContaining)
      );
      expect(comp.telegramGroupsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const customerTelegram: ICustomerTelegram = { id: 456 };
      const customer: ICustomers = { id: 74116 };
      customerTelegram.customer = customer;
      const telegramGroup: ITelegramGroup = { id: 80820 };
      customerTelegram.telegramGroups = [telegramGroup];

      activatedRoute.data = of({ customerTelegram });
      comp.ngOnInit();

      expect(comp.customersSharedCollection).toContain(customer);
      expect(comp.telegramGroupsSharedCollection).toContain(telegramGroup);
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
    describe('compareCustomers', () => {
      it('Should forward to customersService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(customersService, 'compareCustomers');
        comp.compareCustomers(entity, entity2);
        expect(customersService.compareCustomers).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTelegramGroup', () => {
      it('Should forward to telegramGroupService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(telegramGroupService, 'compareTelegramGroup');
        comp.compareTelegramGroup(entity, entity2);
        expect(telegramGroupService.compareTelegramGroup).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
