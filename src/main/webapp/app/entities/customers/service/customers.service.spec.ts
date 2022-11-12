import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICustomers } from '../customers.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../customers.test-samples';

import { CustomersService } from './customers.service';

const requireRestSample: ICustomers = {
  ...sampleWithRequiredData,
};

describe('Customers Service', () => {
  let service: CustomersService;
  let httpMock: HttpTestingController;
  let expectedResult: ICustomers | ICustomers[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CustomersService);
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

    it('should create a Customers', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const customers = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(customers).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Customers', () => {
      const customers = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(customers).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Customers', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Customers', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Customers', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCustomersToCollectionIfMissing', () => {
      it('should add a Customers to an empty array', () => {
        const customers: ICustomers = sampleWithRequiredData;
        expectedResult = service.addCustomersToCollectionIfMissing([], customers);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(customers);
      });

      it('should not add a Customers to an array that contains it', () => {
        const customers: ICustomers = sampleWithRequiredData;
        const customersCollection: ICustomers[] = [
          {
            ...customers,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCustomersToCollectionIfMissing(customersCollection, customers);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Customers to an array that doesn't contain it", () => {
        const customers: ICustomers = sampleWithRequiredData;
        const customersCollection: ICustomers[] = [sampleWithPartialData];
        expectedResult = service.addCustomersToCollectionIfMissing(customersCollection, customers);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(customers);
      });

      it('should add only unique Customers to an array', () => {
        const customersArray: ICustomers[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const customersCollection: ICustomers[] = [sampleWithRequiredData];
        expectedResult = service.addCustomersToCollectionIfMissing(customersCollection, ...customersArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const customers: ICustomers = sampleWithRequiredData;
        const customers2: ICustomers = sampleWithPartialData;
        expectedResult = service.addCustomersToCollectionIfMissing([], customers, customers2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(customers);
        expect(expectedResult).toContain(customers2);
      });

      it('should accept null and undefined values', () => {
        const customers: ICustomers = sampleWithRequiredData;
        expectedResult = service.addCustomersToCollectionIfMissing([], null, customers, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(customers);
      });

      it('should return initial array if no Customers is added', () => {
        const customersCollection: ICustomers[] = [sampleWithRequiredData];
        expectedResult = service.addCustomersToCollectionIfMissing(customersCollection, undefined, null);
        expect(expectedResult).toEqual(customersCollection);
      });
    });

    describe('compareCustomers', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCustomers(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCustomers(entity1, entity2);
        const compareResult2 = service.compareCustomers(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCustomers(entity1, entity2);
        const compareResult2 = service.compareCustomers(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCustomers(entity1, entity2);
        const compareResult2 = service.compareCustomers(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
