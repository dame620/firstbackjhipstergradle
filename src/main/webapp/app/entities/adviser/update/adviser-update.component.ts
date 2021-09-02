import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IAdviser, Adviser } from '../adviser.model';
import { AdviserService } from '../service/adviser.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IBank } from 'app/entities/bank/bank.model';
import { BankService } from 'app/entities/bank/service/bank.service';

@Component({
  selector: 'jhi-adviser-update',
  templateUrl: './adviser-update.component.html',
})
export class AdviserUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  banksSharedCollection: IBank[] = [];

  editForm = this.fb.group({
    id: [],
    registrationNumber: [],
    company: [],
    department: [],
    user: [],
    bank: [],
  });

  constructor(
    protected adviserService: AdviserService,
    protected userService: UserService,
    protected bankService: BankService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ adviser }) => {
      this.updateForm(adviser);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const adviser = this.createFromForm();
    if (adviser.id !== undefined) {
      this.subscribeToSaveResponse(this.adviserService.update(adviser));
    } else {
      this.subscribeToSaveResponse(this.adviserService.create(adviser));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  trackBankById(index: number, item: IBank): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAdviser>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(adviser: IAdviser): void {
    this.editForm.patchValue({
      id: adviser.id,
      registrationNumber: adviser.registrationNumber,
      company: adviser.company,
      department: adviser.department,
      user: adviser.user,
      bank: adviser.bank,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, adviser.user);
    this.banksSharedCollection = this.bankService.addBankToCollectionIfMissing(this.banksSharedCollection, adviser.bank);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.bankService
      .query()
      .pipe(map((res: HttpResponse<IBank[]>) => res.body ?? []))
      .pipe(map((banks: IBank[]) => this.bankService.addBankToCollectionIfMissing(banks, this.editForm.get('bank')!.value)))
      .subscribe((banks: IBank[]) => (this.banksSharedCollection = banks));
  }

  protected createFromForm(): IAdviser {
    return {
      ...new Adviser(),
      id: this.editForm.get(['id'])!.value,
      registrationNumber: this.editForm.get(['registrationNumber'])!.value,
      company: this.editForm.get(['company'])!.value,
      department: this.editForm.get(['department'])!.value,
      user: this.editForm.get(['user'])!.value,
      bank: this.editForm.get(['bank'])!.value,
    };
  }
}
