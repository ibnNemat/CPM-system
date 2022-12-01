import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICustomerTelegram } from '../customer-telegram.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../customer-telegram.test-samples';

import { CustomerTelegramService } from './customer-telegram.service';

const requireRestSample: ICustomerTelegram = {
  ...sampleWithRequiredData,
};

describe('CustomerTelegram Service', () => {
  let service: CustomerTelegramService;
  let httpMock: HttpTestingController;
  let expectedResult: ICustomerTelegram | ICustomerTelegram[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CustomerTelegramService);
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

    it('should create a CustomerTelegram', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const customerTelegram = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(customerTelegram).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CustomerTelegram', () => {
      const customerTelegram = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(customerTelegram).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CustomerTelegram', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CustomerTelegram', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a CustomerTelegram', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCustomerTelegramToCollectionIfMissing', () => {
      it('should add a CustomerTelegram to an empty array', () => {
        const customerTelegram: ICustomerTelegram = sampleWithRequiredData;
        expectedResult = service.addCustomerTelegramToCollectionIfMissing([], customerTelegram);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(customerTelegram);
      });

      it('should not add a CustomerTelegram to an array that contains it', () => {
        const customerTelegram: ICustomerTelegram = sampleWithRequiredData;
        const customerTelegramCollection: ICustomerTelegram[] = [
          {
            ...customerTelegram,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCustomerTelegramToCollectionIfMissing(customerTelegramCollection, customerTelegram);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CustomerTelegram to an array that doesn't contain it", () => {
        const customerTelegram: ICustomerTelegram = sampleWithRequiredData;
        const customerTelegramCollection: ICustomerTelegram[] = [sampleWithPartialData];
        expectedResult = service.addCustomerTelegramToCollectionIfMissing(customerTelegramCollection, customerTelegram);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(customerTelegram);
      });

      it('should add only unique CustomerTelegram to an array', () => {
        const customerTelegramArray: ICustomerTelegram[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const customerTelegramCollection: ICustomerTelegram[] = [sampleWithRequiredData];
        expectedResult = service.addCustomerTelegramToCollectionIfMissing(customerTelegramCollection, ...customerTelegramArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const customerTelegram: ICustomerTelegram = sampleWithRequiredData;
        const customerTelegram2: ICustomerTelegram = sampleWithPartialData;
        expectedResult = service.addCustomerTelegramToCollectionIfMissing([], customerTelegram, customerTelegram2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(customerTelegram);
        expect(expectedResult).toContain(customerTelegram2);
      });

      it('should accept null and undefined values', () => {
        const customerTelegram: ICustomerTelegram = sampleWithRequiredData;
        expectedResult = service.addCustomerTelegramToCollectionIfMissing([], null, customerTelegram, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(customerTelegram);
      });

      it('should return initial array if no CustomerTelegram is added', () => {
        const customerTelegramCollection: ICustomerTelegram[] = [sampleWithRequiredData];
        expectedResult = service.addCustomerTelegramToCollectionIfMissing(customerTelegramCollection, undefined, null);
        expect(expectedResult).toEqual(customerTelegramCollection);
      });
    });

    describe('compareCustomerTelegram', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCustomerTelegram(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCustomerTelegram(entity1, entity2);
        const compareResult2 = service.compareCustomerTelegram(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCustomerTelegram(entity1, entity2);
        const compareResult2 = service.compareCustomerTelegram(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCustomerTelegram(entity1, entity2);
        const compareResult2 = service.compareCustomerTelegram(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
