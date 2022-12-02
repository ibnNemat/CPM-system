import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BotTokenFormService } from './bot-token-form.service';
import { BotTokenService } from '../service/bot-token.service';
import { IBotToken } from '../bot-token.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { BotTokenUpdateComponent } from './bot-token-update.component';

describe('BotToken Management Update Component', () => {
  let comp: BotTokenUpdateComponent;
  let fixture: ComponentFixture<BotTokenUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let botTokenFormService: BotTokenFormService;
  let botTokenService: BotTokenService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BotTokenUpdateComponent],
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
      .overrideTemplate(BotTokenUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BotTokenUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    botTokenFormService = TestBed.inject(BotTokenFormService);
    botTokenService = TestBed.inject(BotTokenService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const botToken: IBotToken = { id: 456 };
      const createdBy: IUser = { id: 24431 };
      botToken.createdBy = createdBy;

      const userCollection: IUser[] = [{ id: 71359 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [createdBy];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ botToken });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const botToken: IBotToken = { id: 456 };
      const createdBy: IUser = { id: 94233 };
      botToken.createdBy = createdBy;

      activatedRoute.data = of({ botToken });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(createdBy);
      expect(comp.botToken).toEqual(botToken);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBotToken>>();
      const botToken = { id: 123 };
      jest.spyOn(botTokenFormService, 'getBotToken').mockReturnValue(botToken);
      jest.spyOn(botTokenService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ botToken });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: botToken }));
      saveSubject.complete();

      // THEN
      expect(botTokenFormService.getBotToken).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(botTokenService.update).toHaveBeenCalledWith(expect.objectContaining(botToken));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBotToken>>();
      const botToken = { id: 123 };
      jest.spyOn(botTokenFormService, 'getBotToken').mockReturnValue({ id: null });
      jest.spyOn(botTokenService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ botToken: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: botToken }));
      saveSubject.complete();

      // THEN
      expect(botTokenFormService.getBotToken).toHaveBeenCalled();
      expect(botTokenService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBotToken>>();
      const botToken = { id: 123 };
      jest.spyOn(botTokenService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ botToken });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(botTokenService.update).toHaveBeenCalled();
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
