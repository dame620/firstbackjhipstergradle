jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { AdviserService } from '../service/adviser.service';
import { IAdviser, Adviser } from '../adviser.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IBank } from 'app/entities/bank/bank.model';
import { BankService } from 'app/entities/bank/service/bank.service';

import { AdviserUpdateComponent } from './adviser-update.component';

describe('Component Tests', () => {
  describe('Adviser Management Update Component', () => {
    let comp: AdviserUpdateComponent;
    let fixture: ComponentFixture<AdviserUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let adviserService: AdviserService;
    let userService: UserService;
    let bankService: BankService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [AdviserUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(AdviserUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AdviserUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      adviserService = TestBed.inject(AdviserService);
      userService = TestBed.inject(UserService);
      bankService = TestBed.inject(BankService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call User query and add missing value', () => {
        const adviser: IAdviser = { id: 456 };
        const user: IUser = { id: 97348 };
        adviser.user = user;

        const userCollection: IUser[] = [{ id: 33107 }];
        jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [user];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ adviser });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Bank query and add missing value', () => {
        const adviser: IAdviser = { id: 456 };
        const bank: IBank = { id: 4623 };
        adviser.bank = bank;

        const bankCollection: IBank[] = [{ id: 80886 }];
        jest.spyOn(bankService, 'query').mockReturnValue(of(new HttpResponse({ body: bankCollection })));
        const additionalBanks = [bank];
        const expectedCollection: IBank[] = [...additionalBanks, ...bankCollection];
        jest.spyOn(bankService, 'addBankToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ adviser });
        comp.ngOnInit();

        expect(bankService.query).toHaveBeenCalled();
        expect(bankService.addBankToCollectionIfMissing).toHaveBeenCalledWith(bankCollection, ...additionalBanks);
        expect(comp.banksSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const adviser: IAdviser = { id: 456 };
        const user: IUser = { id: 7279 };
        adviser.user = user;
        const bank: IBank = { id: 90706 };
        adviser.bank = bank;

        activatedRoute.data = of({ adviser });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(adviser));
        expect(comp.usersSharedCollection).toContain(user);
        expect(comp.banksSharedCollection).toContain(bank);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Adviser>>();
        const adviser = { id: 123 };
        jest.spyOn(adviserService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ adviser });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: adviser }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(adviserService.update).toHaveBeenCalledWith(adviser);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Adviser>>();
        const adviser = new Adviser();
        jest.spyOn(adviserService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ adviser });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: adviser }));
        saveSubject.complete();

        // THEN
        expect(adviserService.create).toHaveBeenCalledWith(adviser);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Adviser>>();
        const adviser = { id: 123 };
        jest.spyOn(adviserService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ adviser });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(adviserService.update).toHaveBeenCalledWith(adviser);
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

      describe('trackBankById', () => {
        it('Should return tracked Bank primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackBankById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
