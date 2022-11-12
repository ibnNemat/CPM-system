import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IGroups } from '../groups.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../groups.test-samples';

import { GroupsService } from './groups.service';

const requireRestSample: IGroups = {
  ...sampleWithRequiredData,
};

describe('Groups Service', () => {
  let service: GroupsService;
  let httpMock: HttpTestingController;
  let expectedResult: IGroups | IGroups[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(GroupsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Groups', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const groups = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(groups).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Groups', () => {
      const groups = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(groups).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Groups', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Groups', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Groups', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addGroupsToCollectionIfMissing', () => {
      it('should add a Groups to an empty array', () => {
        const groups: IGroups = sampleWithRequiredData;
        expectedResult = service.addGroupsToCollectionIfMissing([], groups);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(groups);
      });

      it('should not add a Groups to an array that contains it', () => {
        const groups: IGroups = sampleWithRequiredData;
        const groupsCollection: IGroups[] = [
          {
            ...groups,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addGroupsToCollectionIfMissing(groupsCollection, groups);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Groups to an array that doesn't contain it", () => {
        const groups: IGroups = sampleWithRequiredData;
        const groupsCollection: IGroups[] = [sampleWithPartialData];
        expectedResult = service.addGroupsToCollectionIfMissing(groupsCollection, groups);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(groups);
      });

      it('should add only unique Groups to an array', () => {
        const groupsArray: IGroups[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const groupsCollection: IGroups[] = [sampleWithRequiredData];
        expectedResult = service.addGroupsToCollectionIfMissing(groupsCollection, ...groupsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const groups: IGroups = sampleWithRequiredData;
        const groups2: IGroups = sampleWithPartialData;
        expectedResult = service.addGroupsToCollectionIfMissing([], groups, groups2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(groups);
        expect(expectedResult).toContain(groups2);
      });

      it('should accept null and undefined values', () => {
        const groups: IGroups = sampleWithRequiredData;
        expectedResult = service.addGroupsToCollectionIfMissing([], null, groups, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(groups);
      });

      it('should return initial array if no Groups is added', () => {
        const groupsCollection: IGroups[] = [sampleWithRequiredData];
        expectedResult = service.addGroupsToCollectionIfMissing(groupsCollection, undefined, null);
        expect(expectedResult).toEqual(groupsCollection);
      });
    });

    describe('compareGroups', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareGroups(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareGroups(entity1, entity2);
        const compareResult2 = service.compareGroups(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareGroups(entity1, entity2);
        const compareResult2 = service.compareGroups(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareGroups(entity1, entity2);
        const compareResult2 = service.compareGroups(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
