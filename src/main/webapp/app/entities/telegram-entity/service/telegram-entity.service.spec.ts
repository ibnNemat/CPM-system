import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITelegramEntity } from '../telegram-entity.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../telegram-entity.test-samples';

import { TelegramEntityService } from './telegram-entity.service';

const requireRestSample: ITelegramEntity = {
  ...sampleWithRequiredData,
};

describe('TelegramEntity Service', () => {
  let service: TelegramEntityService;
  let httpMock: HttpTestingController;
  let expectedResult: ITelegramEntity | ITelegramEntity[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TelegramEntityService);
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

    it('should create a TelegramEntity', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const telegramEntity = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(telegramEntity).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TelegramEntity', () => {
      const telegramEntity = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(telegramEntity).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TelegramEntity', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TelegramEntity', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TelegramEntity', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTelegramEntityToCollectionIfMissing', () => {
      it('should add a TelegramEntity to an empty array', () => {
        const telegramEntity: ITelegramEntity = sampleWithRequiredData;
        expectedResult = service.addTelegramEntityToCollectionIfMissing([], telegramEntity);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(telegramEntity);
      });

      it('should not add a TelegramEntity to an array that contains it', () => {
        const telegramEntity: ITelegramEntity = sampleWithRequiredData;
        const telegramEntityCollection: ITelegramEntity[] = [
          {
            ...telegramEntity,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTelegramEntityToCollectionIfMissing(telegramEntityCollection, telegramEntity);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TelegramEntity to an array that doesn't contain it", () => {
        const telegramEntity: ITelegramEntity = sampleWithRequiredData;
        const telegramEntityCollection: ITelegramEntity[] = [sampleWithPartialData];
        expectedResult = service.addTelegramEntityToCollectionIfMissing(telegramEntityCollection, telegramEntity);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(telegramEntity);
      });

      it('should add only unique TelegramEntity to an array', () => {
        const telegramEntityArray: ITelegramEntity[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const telegramEntityCollection: ITelegramEntity[] = [sampleWithRequiredData];
        expectedResult = service.addTelegramEntityToCollectionIfMissing(telegramEntityCollection, ...telegramEntityArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const telegramEntity: ITelegramEntity = sampleWithRequiredData;
        const telegramEntity2: ITelegramEntity = sampleWithPartialData;
        expectedResult = service.addTelegramEntityToCollectionIfMissing([], telegramEntity, telegramEntity2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(telegramEntity);
        expect(expectedResult).toContain(telegramEntity2);
      });

      it('should accept null and undefined values', () => {
        const telegramEntity: ITelegramEntity = sampleWithRequiredData;
        expectedResult = service.addTelegramEntityToCollectionIfMissing([], null, telegramEntity, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(telegramEntity);
      });

      it('should return initial array if no TelegramEntity is added', () => {
        const telegramEntityCollection: ITelegramEntity[] = [sampleWithRequiredData];
        expectedResult = service.addTelegramEntityToCollectionIfMissing(telegramEntityCollection, undefined, null);
        expect(expectedResult).toEqual(telegramEntityCollection);
      });
    });

    describe('compareTelegramEntity', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTelegramEntity(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTelegramEntity(entity1, entity2);
        const compareResult2 = service.compareTelegramEntity(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTelegramEntity(entity1, entity2);
        const compareResult2 = service.compareTelegramEntity(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTelegramEntity(entity1, entity2);
        const compareResult2 = service.compareTelegramEntity(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
