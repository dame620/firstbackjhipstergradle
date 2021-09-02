import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IManager, Manager } from '../manager.model';
import { ManagerService } from '../service/manager.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ICompany } from 'app/entities/company/company.model';
import { CompanyService } from 'app/entities/company/service/company.service';

@Component({
  selector: 'jhi-manager-update',
  templateUrl: './manager-update.component.html',
})
export class ManagerUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  companiesSharedCollection: ICompany[] = [];

  editForm = this.fb.group({
    id: [],
    registrationNumber: [],
    department: [],
    user: [],
    company: [],
  });

  constructor(
    protected managerService: ManagerService,
    protected userService: UserService,
    protected companyService: CompanyService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ manager }) => {
      this.updateForm(manager);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const manager = this.createFromForm();
    if (manager.id !== undefined) {
      this.subscribeToSaveResponse(this.managerService.update(manager));
    } else {
      this.subscribeToSaveResponse(this.managerService.create(manager));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  trackCompanyById(index: number, item: ICompany): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IManager>>): void {
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

  protected updateForm(manager: IManager): void {
    this.editForm.patchValue({
      id: manager.id,
      registrationNumber: manager.registrationNumber,
      department: manager.department,
      user: manager.user,
      company: manager.company,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, manager.user);
    this.companiesSharedCollection = this.companyService.addCompanyToCollectionIfMissing(this.companiesSharedCollection, manager.company);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.companyService
      .query()
      .pipe(map((res: HttpResponse<ICompany[]>) => res.body ?? []))
      .pipe(
        map((companies: ICompany[]) => this.companyService.addCompanyToCollectionIfMissing(companies, this.editForm.get('company')!.value))
      )
      .subscribe((companies: ICompany[]) => (this.companiesSharedCollection = companies));
  }

  protected createFromForm(): IManager {
    return {
      ...new Manager(),
      id: this.editForm.get(['id'])!.value,
      registrationNumber: this.editForm.get(['registrationNumber'])!.value,
      department: this.editForm.get(['department'])!.value,
      user: this.editForm.get(['user'])!.value,
      company: this.editForm.get(['company'])!.value,
    };
  }
}
