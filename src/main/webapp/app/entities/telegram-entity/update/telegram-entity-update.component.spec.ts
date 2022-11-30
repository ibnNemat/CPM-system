import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TelegramEntityFormService } from './telegram-entity-form.service';
import { TelegramEntityService } from '../service/telegram-entity.service';
import { ITelegramEntity } from '../telegram-entity.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { TelegramEntityUpdateComponent } from './telegram-entity-update.component';

describe('TelegramEntity Management Update Component', () => {
  let comp: TelegramEntityUpdateComponent;
  let fixture: ComponentFixture<TelegramEntityUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let telegramEntityFormService: TelegramEntityFormService;
  let telegramEntityService: TelegramEntityService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TelegramEntityUpdateComponent],
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
      .overrideTemplate(TelegramEntityUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TelegramEntityUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    telegramEntityFormService = TestBed.inject(TelegramEntityFormService);
    telegramEntityService = TestBed.inject(TelegramEntityService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const telegramEntity: ITelegramEntity = { id: 456 };
      const user: IUser = { id: 3915 };
      telegramEntity.user = user;

      const userCollection: IUser[] = [{ id: 49103 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ telegramEntity });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const telegramEntity: ITelegramEntity = { id: 456 };
      const user: IUser = { id: 48558 };
      telegramEntity.user = user;

      activatedRoute.data = of({ telegramEntity });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.telegramEntity).toEqual(telegramEntity);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITelegramEntity>>();
      const telegramEntity = { id: 123 };
      jest.spyOn(telegramEntityFormService, 'getTelegramEntity').mockReturnValue(telegramEntity);
      jest.spyOn(telegramEntityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ telegramEntity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: telegramEntity }));
      saveSubject.complete();

      // THEN
      expect(telegramEntityFormService.getTelegramEntity).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(telegramEntityService.update).toHaveBeenCalledWith(expect.objectContaining(telegramEntity));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITelegramEntity>>();
      const telegramEntity = { id: 123 };
      jest.spyOn(telegramEntityFormService, 'getTelegramEntity').mockReturnValue({ id: null });
      jest.spyOn(telegramEntityService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ telegramEntity: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: telegramEntity }));
      saveSubject.complete();

      // THEN
      expect(telegramEntityFormService.getTelegramEntity).toHaveBeenCalled();
      expect(telegramEntityService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITelegramEntity>>();
      const telegramEntity = { id: 123 };
      jest.spyOn(telegramEntityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ telegramEntity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(telegramEntityService.update).toHaveBeenCalled();
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
