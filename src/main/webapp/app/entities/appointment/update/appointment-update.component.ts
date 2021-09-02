import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IAppointment, Appointment } from '../appointment.model';
import { AppointmentService } from '../service/appointment.service';
import { IAdviser } from 'app/entities/adviser/adviser.model';
import { AdviserService } from 'app/entities/adviser/service/adviser.service';
import { IManager } from 'app/entities/manager/manager.model';
import { ManagerService } from 'app/entities/manager/service/manager.service';

@Component({
  selector: 'jhi-appointment-update',
  templateUrl: './appointment-update.component.html',
})
export class AppointmentUpdateComponent implements OnInit {
  isSaving = false;

  advisersSharedCollection: IAdviser[] = [];
  managersSharedCollection: IManager[] = [];

  editForm = this.fb.group({
    id: [],
    reason: [],
    date: [],
    state: [],
    reportreason: [],
    adviser: [],
    manager: [],
  });

  constructor(
    protected appointmentService: AppointmentService,
    protected adviserService: AdviserService,
    protected managerService: ManagerService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appointment }) => {
      if (appointment.id === undefined) {
        const today = dayjs().startOf('day');
        appointment.date = today;
      }

      this.updateForm(appointment);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const appointment = this.createFromForm();
    if (appointment.id !== undefined) {
      this.subscribeToSaveResponse(this.appointmentService.update(appointment));
    } else {
      this.subscribeToSaveResponse(this.appointmentService.create(appointment));
    }
  }

  trackAdviserById(index: number, item: IAdviser): number {
    return item.id!;
  }

  trackManagerById(index: number, item: IManager): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppointment>>): void {
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

  protected updateForm(appointment: IAppointment): void {
    this.editForm.patchValue({
      id: appointment.id,
      reason: appointment.reason,
      date: appointment.date ? appointment.date.format(DATE_TIME_FORMAT) : null,
      state: appointment.state,
      reportreason: appointment.reportreason,
      adviser: appointment.adviser,
      manager: appointment.manager,
    });

    this.advisersSharedCollection = this.adviserService.addAdviserToCollectionIfMissing(this.advisersSharedCollection, appointment.adviser);
    this.managersSharedCollection = this.managerService.addManagerToCollectionIfMissing(this.managersSharedCollection, appointment.manager);
  }

  protected loadRelationshipsOptions(): void {
    this.adviserService
      .query()
      .pipe(map((res: HttpResponse<IAdviser[]>) => res.body ?? []))
      .pipe(
        map((advisers: IAdviser[]) => this.adviserService.addAdviserToCollectionIfMissing(advisers, this.editForm.get('adviser')!.value))
      )
      .subscribe((advisers: IAdviser[]) => (this.advisersSharedCollection = advisers));

    this.managerService
      .query()
      .pipe(map((res: HttpResponse<IManager[]>) => res.body ?? []))
      .pipe(
        map((managers: IManager[]) => this.managerService.addManagerToCollectionIfMissing(managers, this.editForm.get('manager')!.value))
      )
      .subscribe((managers: IManager[]) => (this.managersSharedCollection = managers));
  }

  protected createFromForm(): IAppointment {
    return {
      ...new Appointment(),
      id: this.editForm.get(['id'])!.value,
      reason: this.editForm.get(['reason'])!.value,
      date: this.editForm.get(['date'])!.value ? dayjs(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      state: this.editForm.get(['state'])!.value,
      reportreason: this.editForm.get(['reportreason'])!.value,
      adviser: this.editForm.get(['adviser'])!.value,
      manager: this.editForm.get(['manager'])!.value,
    };
  }
}
