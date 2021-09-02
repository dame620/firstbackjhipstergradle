jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IAdviser, Adviser } from '../adviser.model';
import { AdviserService } from '../service/adviser.service';

import { AdviserRoutingResolveService } from './adviser-routing-resolve.service';

describe('Service Tests', () => {
  describe('Adviser routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: AdviserRoutingResolveService;
    let service: AdviserService;
    let resultAdviser: IAdviser | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(AdviserRoutingResolveService);
      service = TestBed.inject(AdviserService);
      resultAdviser = undefined;
    });

    describe('resolve', () => {
      it('should return IAdviser returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultAdviser = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultAdviser).toEqual({ id: 123 });
      });

      it('should return new IAdviser if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultAdviser = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultAdviser).toEqual(new Adviser());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Adviser })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultAdviser = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultAdviser).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
