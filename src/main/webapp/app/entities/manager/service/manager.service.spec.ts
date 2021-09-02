import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IManager, Manager } from '../manager.model';

import { ManagerService } from './manager.service';

describe('Service Tests', () => {
  describe('Manager Service', () => {
    let service: ManagerService;
    let httpMock: HttpTestingController;
    let elemDefault: IManager;
    let expectedResult: IManager | IManager[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(ManagerService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        registrationNumber: 'AAAAAAA',
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

      it('should create a Manager', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Manager()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Manager', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            registrationNumber: 'BBBBBB',
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

      it('should partial update a Manager', () => {
        const patchObject = Object.assign({}, new Manager());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Manager', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            registrationNumber: 'BBBBBB',
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

      it('should delete a Manager', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addManagerToCollectionIfMissing', () => {
        it('should add a Manager to an empty array', () => {
          const manager: IManager = { id: 123 };
          expectedResult = service.addManagerToCollectionIfMissing([], manager);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(manager);
        });

        it('should not add a Manager to an array that contains it', () => {
          const manager: IManager = { id: 123 };
          const managerCollection: IManager[] = [
            {
              ...manager,
            },
            { id: 456 },
          ];
          expectedResult = service.addManagerToCollectionIfMissing(managerCollection, manager);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Manager to an array that doesn't contain it", () => {
          const manager: IManager = { id: 123 };
          const managerCollection: IManager[] = [{ id: 456 }];
          expectedResult = service.addManagerToCollectionIfMissing(managerCollection, manager);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(manager);
        });

        it('should add only unique Manager to an array', () => {
          const managerArray: IManager[] = [{ id: 123 }, { id: 456 }, { id: 83337 }];
          const managerCollection: IManager[] = [{ id: 123 }];
          expectedResult = service.addManagerToCollectionIfMissing(managerCollection, ...managerArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const manager: IManager = { id: 123 };
          const manager2: IManager = { id: 456 };
          expectedResult = service.addManagerToCollectionIfMissing([], manager, manager2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(manager);
          expect(expectedResult).toContain(manager2);
        });

        it('should accept null and undefined values', () => {
          const manager: IManager = { id: 123 };
          expectedResult = service.addManagerToCollectionIfMissing([], null, manager, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(manager);
        });

        it('should return initial array if no Manager is added', () => {
          const managerCollection: IManager[] = [{ id: 123 }];
          expectedResult = service.addManagerToCollectionIfMissing(managerCollection, undefined, null);
          expect(expectedResult).toEqual(managerCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
