import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IServices } from '../services.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../services.test-samples';

import { ServicesService } from './services.service';

const requireRestSample: IServices = {
  ...sampleWithRequiredData,
};

describe('Services Service', () => {
  let service: ServicesService;
  let httpMock: HttpTestingController;
  let expectedResult: IServices | IServices[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ServicesService);
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

    it('should create a Services', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const services = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(services).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Services', () => {
      const services = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(services).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Services', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Services', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Services', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addServicesToCollectionIfMissing', () => {
      it('should add a Services to an empty array', () => {
        const services: IServices = sampleWithRequiredData;
        expectedResult = service.addServicesToCollectionIfMissing([], services);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(services);
      });

      it('should not add a Services to an array that contains it', () => {
        const services: IServices = sampleWithRequiredData;
        const servicesCollection: IServices[] = [
          {
            ...services,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addServicesToCollectionIfMissing(servicesCollection, services);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Services to an array that doesn't contain it", () => {
        const services: IServices = sampleWithRequiredData;
        const servicesCollection: IServices[] = [sampleWithPartialData];
        expectedResult = service.addServicesToCollectionIfMissing(servicesCollection, services);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(services);
      });

      it('should add only unique Services to an array', () => {
        const servicesArray: IServices[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const servicesCollection: IServices[] = [sampleWithRequiredData];
        expectedResult = service.addServicesToCollectionIfMissing(servicesCollection, ...servicesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const services: IServices = sampleWithRequiredData;
        const services2: IServices = sampleWithPartialData;
        expectedResult = service.addServicesToCollectionIfMissing([], services, services2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(services);
        expect(expectedResult).toContain(services2);
      });

      it('should accept null and undefined values', () => {
        const services: IServices = sampleWithRequiredData;
        expectedResult = service.addServicesToCollectionIfMissing([], null, services, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(services);
      });

      it('should return initial array if no Services is added', () => {
        const servicesCollection: IServices[] = [sampleWithRequiredData];
        expectedResult = service.addServicesToCollectionIfMissing(servicesCollection, undefined, null);
        expect(expectedResult).toEqual(servicesCollection);
      });
    });

    describe('compareServices', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareServices(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareServices(entity1, entity2);
        const compareResult2 = service.compareServices(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareServices(entity1, entity2);
        const compareResult2 = service.compareServices(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareServices(entity1, entity2);
        const compareResult2 = service.compareServices(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
