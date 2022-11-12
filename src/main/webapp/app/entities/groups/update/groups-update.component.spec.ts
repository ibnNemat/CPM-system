import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GroupsFormService } from './groups-form.service';
import { GroupsService } from '../service/groups.service';
import { IGroups } from '../groups.model';
import { IOrganization } from 'app/entities/organization/organization.model';
import { OrganizationService } from 'app/entities/organization/service/organization.service';

import { GroupsUpdateComponent } from './groups-update.component';

describe('Groups Management Update Component', () => {
  let comp: GroupsUpdateComponent;
  let fixture: ComponentFixture<GroupsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let groupsFormService: GroupsFormService;
  let groupsService: GroupsService;
  let organizationService: OrganizationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [GroupsUpdateComponent],
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
      .overrideTemplate(GroupsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GroupsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    groupsFormService = TestBed.inject(GroupsFormService);
    groupsService = TestBed.inject(GroupsService);
    organizationService = TestBed.inject(OrganizationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Organization query and add missing value', () => {
      const groups: IGroups = { id: 456 };
      const organization: IOrganization = { id: 50891 };
      groups.organization = organization;

      const organizationCollection: IOrganization[] = [{ id: 69494 }];
      jest.spyOn(organizationService, 'query').mockReturnValue(of(new HttpResponse({ body: organizationCollection })));
      const additionalOrganizations = [organization];
      const expectedCollection: IOrganization[] = [...additionalOrganizations, ...organizationCollection];
      jest.spyOn(organizationService, 'addOrganizationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ groups });
      comp.ngOnInit();

      expect(organizationService.query).toHaveBeenCalled();
      expect(organizationService.addOrganizationToCollectionIfMissing).toHaveBeenCalledWith(
        organizationCollection,
        ...additionalOrganizations.map(expect.objectContaining)
      );
      expect(comp.organizationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const groups: IGroups = { id: 456 };
      const organization: IOrganization = { id: 87226 };
      groups.organization = organization;

      activatedRoute.data = of({ groups });
      comp.ngOnInit();

      expect(comp.organizationsSharedCollection).toContain(organization);
      expect(comp.groups).toEqual(groups);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGroups>>();
      const groups = { id: 123 };
      jest.spyOn(groupsFormService, 'getGroups').mockReturnValue(groups);
      jest.spyOn(groupsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ groups });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: groups }));
      saveSubject.complete();

      // THEN
      expect(groupsFormService.getGroups).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(groupsService.update).toHaveBeenCalledWith(expect.objectContaining(groups));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGroups>>();
      const groups = { id: 123 };
      jest.spyOn(groupsFormService, 'getGroups').mockReturnValue({ id: null });
      jest.spyOn(groupsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ groups: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: groups }));
      saveSubject.complete();

      // THEN
      expect(groupsFormService.getGroups).toHaveBeenCalled();
      expect(groupsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGroups>>();
      const groups = { id: 123 };
      jest.spyOn(groupsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ groups });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(groupsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareOrganization', () => {
      it('Should forward to organizationService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(organizationService, 'compareOrganization');
        comp.compareOrganization(entity, entity2);
        expect(organizationService.compareOrganization).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
