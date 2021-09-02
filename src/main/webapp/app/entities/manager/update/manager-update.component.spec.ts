jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ManagerService } from '../service/manager.service';
import { IManager, Manager } from '../manager.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ICompany } from 'app/entities/company/company.model';
import { CompanyService } from 'app/entities/company/service/company.service';

import { ManagerUpdateComponent } from './manager-update.component';

describe('Component Tests', () => {
  describe('Manager Management Update Component', () => {
    let comp: ManagerUpdateComponent;
    let fixture: ComponentFixture<ManagerUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let managerService: ManagerService;
    let userService: UserService;
    let companyService: CompanyService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ManagerUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ManagerUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ManagerUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      managerService = TestBed.inject(ManagerService);
      userService = TestBed.inject(UserService);
      companyService = TestBed.inject(CompanyService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call User query and add missing value', () => {
        const manager: IManager = { id: 456 };
        const user: IUser = { id: 88906 };
        manager.user = user;

        const userCollection: IUser[] = [{ id: 59580 }];
        jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [user];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ manager });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Company query and add missing value', () => {
        const manager: IManager = { id: 456 };
        const company: ICompany = { id: 49074 };
        manager.company = company;

        const companyCollection: ICompany[] = [{ id: 45725 }];
        jest.spyOn(companyService, 'query').mockReturnValue(of(new HttpResponse({ body: companyCollection })));
        const additionalCompanies = [company];
        const expectedCollection: ICompany[] = [...additionalCompanies, ...companyCollection];
        jest.spyOn(companyService, 'addCompanyToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ manager });
        comp.ngOnInit();

        expect(companyService.query).toHaveBeenCalled();
        expect(companyService.addCompanyToCollectionIfMissing).toHaveBeenCalledWith(companyCollection, ...additionalCompanies);
        expect(comp.companiesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const manager: IManager = { id: 456 };
        const user: IUser = { id: 53222 };
        manager.user = user;
        const company: ICompany = { id: 86529 };
        manager.company = company;

        activatedRoute.data = of({ manager });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(manager));
        expect(comp.usersSharedCollection).toContain(user);
        expect(comp.companiesSharedCollection).toContain(company);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Manager>>();
        const manager = { id: 123 };
        jest.spyOn(managerService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ manager });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: manager }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(managerService.update).toHaveBeenCalledWith(manager);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Manager>>();
        const manager = new Manager();
        jest.spyOn(managerService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ manager });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: manager }));
        saveSubject.complete();

        // THEN
        expect(managerService.create).toHaveBeenCalledWith(manager);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Manager>>();
        const manager = { id: 123 };
        jest.spyOn(managerService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ manager });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(managerService.update).toHaveBeenCalledWith(manager);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackUserById', () => {
        it('Should return tracked User primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackUserById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackCompanyById', () => {
        it('Should return tracked Company primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackCompanyById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
