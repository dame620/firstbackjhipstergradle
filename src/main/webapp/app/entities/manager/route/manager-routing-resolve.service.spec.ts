jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IManager, Manager } from '../manager.model';
import { ManagerService } from '../service/manager.service';

import { ManagerRoutingResolveService } from './manager-routing-resolve.service';

describe('Service Tests', () => {
  describe('Manager routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: ManagerRoutingResolveService;
    let service: ManagerService;
    let resultManager: IManager | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(ManagerRoutingResolveService);
      service = TestBed.inject(ManagerService);
      resultManager = undefined;
    });

    describe('resolve', () => {
      it('should return IManager returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultManager = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultManager).toEqual({ id: 123 });
      });

      it('should return new IManager if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultManager = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultManager).toEqual(new Manager());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Manager })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultManager = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultManager).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
