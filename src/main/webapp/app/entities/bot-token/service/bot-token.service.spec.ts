import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IBotToken } from '../bot-token.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../bot-token.test-samples';

import { BotTokenService } from './bot-token.service';

const requireRestSample: IBotToken = {
  ...sampleWithRequiredData,
};

describe('BotToken Service', () => {
  let service: BotTokenService;
  let httpMock: HttpTestingController;
  let expectedResult: IBotToken | IBotToken[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BotTokenService);
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

    it('should create a BotToken', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const botToken = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(botToken).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BotToken', () => {
      const botToken = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(botToken).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BotToken', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BotToken', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a BotToken', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBotTokenToCollectionIfMissing', () => {
      it('should add a BotToken to an empty array', () => {
        const botToken: IBotToken = sampleWithRequiredData;
        expectedResult = service.addBotTokenToCollectionIfMissing([], botToken);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(botToken);
      });

      it('should not add a BotToken to an array that contains it', () => {
        const botToken: IBotToken = sampleWithRequiredData;
        const botTokenCollection: IBotToken[] = [
          {
            ...botToken,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBotTokenToCollectionIfMissing(botTokenCollection, botToken);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BotToken to an array that doesn't contain it", () => {
        const botToken: IBotToken = sampleWithRequiredData;
        const botTokenCollection: IBotToken[] = [sampleWithPartialData];
        expectedResult = service.addBotTokenToCollectionIfMissing(botTokenCollection, botToken);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(botToken);
      });

      it('should add only unique BotToken to an array', () => {
        const botTokenArray: IBotToken[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const botTokenCollection: IBotToken[] = [sampleWithRequiredData];
        expectedResult = service.addBotTokenToCollectionIfMissing(botTokenCollection, ...botTokenArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const botToken: IBotToken = sampleWithRequiredData;
        const botToken2: IBotToken = sampleWithPartialData;
        expectedResult = service.addBotTokenToCollectionIfMissing([], botToken, botToken2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(botToken);
        expect(expectedResult).toContain(botToken2);
      });

      it('should accept null and undefined values', () => {
        const botToken: IBotToken = sampleWithRequiredData;
        expectedResult = service.addBotTokenToCollectionIfMissing([], null, botToken, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(botToken);
      });

      it('should return initial array if no BotToken is added', () => {
        const botTokenCollection: IBotToken[] = [sampleWithRequiredData];
        expectedResult = service.addBotTokenToCollectionIfMissing(botTokenCollection, undefined, null);
        expect(expectedResult).toEqual(botTokenCollection);
      });
    });

    describe('compareBotToken', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBotToken(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareBotToken(entity1, entity2);
        const compareResult2 = service.compareBotToken(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareBotToken(entity1, entity2);
        const compareResult2 = service.compareBotToken(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareBotToken(entity1, entity2);
        const compareResult2 = service.compareBotToken(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
