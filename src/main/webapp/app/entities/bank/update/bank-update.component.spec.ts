jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { BankService } from '../service/bank.service';
import { IBank, Bank } from '../bank.model';

import { BankUpdateComponent } from './bank-update.component';

describe('Component Tests', () => {
  describe('Bank Management Update Component', () => {
    let comp: BankUpdateComponent;
    let fixture: ComponentFixture<BankUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let bankService: BankService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BankUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(BankUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BankUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      bankService = TestBed.inject(BankService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const bank: IBank = { id: 456 };

        activatedRoute.data = of({ bank });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(bank));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Bank>>();
        const bank = { id: 123 };
        jest.spyOn(bankService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ bank });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: bank }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(bankService.update).toHaveBeenCalledWith(bank);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Bank>>();
        const bank = new Bank();
        jest.spyOn(bankService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ bank });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: bank }));
        saveSubject.complete();

        // THEN
        expect(bankService.create).toHaveBeenCalledWith(bank);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Bank>>();
        const bank = { id: 123 };
        jest.spyOn(bankService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ bank });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(bankService.update).toHaveBeenCalledWith(bank);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
