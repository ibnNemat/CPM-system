import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITelegramGroup } from '../telegram-group.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../telegram-group.test-samples';

import { TelegramGroupService } from './telegram-group.service';

const requireRestSample: ITelegramGroup = {
  ...sampleWithRequiredData,
};

describe('TelegramGroup Service', () => {
  let service: TelegramGroupService;
  let httpMock: HttpTestingController;
  let expectedResult: ITelegramGroup | ITelegramGroup[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TelegramGroupService);
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

    it('should create a TelegramGroup', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const telegramGroup = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(telegramGroup).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TelegramGroup', () => {
      const telegramGroup = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(telegramGroup).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TelegramGroup', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TelegramGroup', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TelegramGroup', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTelegramGroupToCollectionIfMissing', () => {
      it('should add a TelegramGroup to an empty array', () => {
        const telegramGroup: ITelegramGroup = sampleWithRequiredData;
        expectedResult = service.addTelegramGroupToCollectionIfMissing([], telegramGroup);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(telegramGroup);
      });

      it('should not add a TelegramGroup to an array that contains it', () => {
        const telegramGroup: ITelegramGroup = sampleWithRequiredData;
        const telegramGroupCollection: ITelegramGroup[] = [
          {
            ...telegramGroup,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTelegramGroupToCollectionIfMissing(telegramGroupCollection, telegramGroup);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TelegramGroup to an array that doesn't contain it", () => {
        const telegramGroup: ITelegramGroup = sampleWithRequiredData;
        const telegramGroupCollection: ITelegramGroup[] = [sampleWithPartialData];
        expectedResult = service.addTelegramGroupToCollectionIfMissing(telegramGroupCollection, telegramGroup);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(telegramGroup);
      });

      it('should add only unique TelegramGroup to an array', () => {
        const telegramGroupArray: ITelegramGroup[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const telegramGroupCollection: ITelegramGroup[] = [sampleWithRequiredData];
        expectedResult = service.addTelegramGroupToCollectionIfMissing(telegramGroupCollection, ...telegramGroupArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const telegramGroup: ITelegramGroup = sampleWithRequiredData;
        const telegramGroup2: ITelegramGroup = sampleWithPartialData;
        expectedResult = service.addTelegramGroupToCollectionIfMissing([], telegramGroup, telegramGroup2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(telegramGroup);
        expect(expectedResult).toContain(telegramGroup2);
      });

      it('should accept null and undefined values', () => {
        const telegramGroup: ITelegramGroup = sampleWithRequiredData;
        expectedResult = service.addTelegramGroupToCollectionIfMissing([], null, telegramGroup, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(telegramGroup);
      });

      it('should return initial array if no TelegramGroup is added', () => {
        const telegramGroupCollection: ITelegramGroup[] = [sampleWithRequiredData];
        expectedResult = service.addTelegramGroupToCollectionIfMissing(telegramGroupCollection, undefined, null);
        expect(expectedResult).toEqual(telegramGroupCollection);
      });
    });

    describe('compareTelegramGroup', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTelegramGroup(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTelegramGroup(entity1, entity2);
        const compareResult2 = service.compareTelegramGroup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTelegramGroup(entity1, entity2);
        const compareResult2 = service.compareTelegramGroup(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTelegramGroup(entity1, entity2);
        const compareResult2 = service.compareTelegramGroup(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
