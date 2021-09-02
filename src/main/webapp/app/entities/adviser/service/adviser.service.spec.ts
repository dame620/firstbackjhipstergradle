import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAdviser, Adviser } from '../adviser.model';

import { AdviserService } from './adviser.service';

describe('Service Tests', () => {
  describe('Adviser Service', () => {
    let service: AdviserService;
    let httpMock: HttpTestingController;
    let elemDefault: IAdviser;
    let expectedResult: IAdviser | IAdviser[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(AdviserService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        registrationNumber: 'AAAAAAA',
        company: 'AAAAAAA',
        department: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Adviser', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Adviser()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Adviser', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            registrationNumber: 'BBBBBB',
            company: 'BBBBBB',
            department: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Adviser', () => {
        const patchObject = Object.assign({}, new Adviser());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Adviser', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            registrationNumber: 'BBBBBB',
            company: 'BBBBBB',
            department: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Adviser', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addAdviserToCollectionIfMissing', () => {
        it('should add a Adviser to an empty array', () => {
          const adviser: IAdviser = { id: 123 };
          expectedResult = service.addAdviserToCollectionIfMissing([], adviser);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(adviser);
        });

        it('should not add a Adviser to an array that contains it', () => {
          const adviser: IAdviser = { id: 123 };
          const adviserCollection: IAdviser[] = [
            {
              ...adviser,
            },
            { id: 456 },
          ];
          expectedResult = service.addAdviserToCollectionIfMissing(adviserCollection, adviser);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Adviser to an array that doesn't contain it", () => {
          const adviser: IAdviser = { id: 123 };
          const adviserCollection: IAdviser[] = [{ id: 456 }];
          expectedResult = service.addAdviserToCollectionIfMissing(adviserCollection, adviser);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(adviser);
        });

        it('should add only unique Adviser to an array', () => {
          const adviserArray: IAdviser[] = [{ id: 123 }, { id: 456 }, { id: 19013 }];
          const adviserCollection: IAdviser[] = [{ id: 123 }];
          expectedResult = service.addAdviserToCollectionIfMissing(adviserCollection, ...adviserArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const adviser: IAdviser = { id: 123 };
          const adviser2: IAdviser = { id: 456 };
          expectedResult = service.addAdviserToCollectionIfMissing([], adviser, adviser2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(adviser);
          expect(expectedResult).toContain(adviser2);
        });

        it('should accept null and undefined values', () => {
          const adviser: IAdviser = { id: 123 };
          expectedResult = service.addAdviserToCollectionIfMissing([], null, adviser, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(adviser);
        });

        it('should return initial array if no Adviser is added', () => {
          const adviserCollection: IAdviser[] = [{ id: 123 }];
          expectedResult = service.addAdviserToCollectionIfMissing(adviserCollection, undefined, null);
          expect(expectedResult).toEqual(adviserCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
