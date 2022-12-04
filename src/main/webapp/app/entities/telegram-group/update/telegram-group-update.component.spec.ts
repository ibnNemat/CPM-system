import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TelegramGroupFormService } from './telegram-group-form.service';
import { TelegramGroupService } from '../service/telegram-group.service';
import { ITelegramGroup } from '../telegram-group.model';

import { TelegramGroupUpdateComponent } from './telegram-group-update.component';

describe('TelegramGroup Management Update Component', () => {
  let comp: TelegramGroupUpdateComponent;
  let fixture: ComponentFixture<TelegramGroupUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let telegramGroupFormService: TelegramGroupFormService;
  let telegramGroupService: TelegramGroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TelegramGroupUpdateComponent],
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
      .overrideTemplate(TelegramGroupUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TelegramGroupUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    telegramGroupFormService = TestBed.inject(TelegramGroupFormService);
    telegramGroupService = TestBed.inject(TelegramGroupService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const telegramGroup: ITelegramGroup = { id: 456 };

      activatedRoute.data = of({ telegramGroup });
      comp.ngOnInit();

      expect(comp.telegramGroup).toEqual(telegramGroup);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITelegramGroup>>();
      const telegramGroup = { id: 123 };
      jest.spyOn(telegramGroupFormService, 'getTelegramGroup').mockReturnValue(telegramGroup);
      jest.spyOn(telegramGroupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ telegramGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: telegramGroup }));
      saveSubject.complete();

      // THEN
      expect(telegramGroupFormService.getTelegramGroup).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(telegramGroupService.update).toHaveBeenCalledWith(expect.objectContaining(telegramGroup));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITelegramGroup>>();
      const telegramGroup = { id: 123 };
      jest.spyOn(telegramGroupFormService, 'getTelegramGroup').mockReturnValue({ id: null });
      jest.spyOn(telegramGroupService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ telegramGroup: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: telegramGroup }));
      saveSubject.complete();

      // THEN
      expect(telegramGroupFormService.getTelegramGroup).toHaveBeenCalled();
      expect(telegramGroupService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITelegramGroup>>();
      const telegramGroup = { id: 123 };
      jest.spyOn(telegramGroupService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ telegramGroup });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(telegramGroupService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
