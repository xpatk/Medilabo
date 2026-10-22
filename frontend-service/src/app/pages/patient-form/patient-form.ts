import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { Patient, PatientPayload } from '../../models/patient';
import { PatientService } from '../../services/patient.service';

@Component({
  selector: 'app-patient-form',
  imports: [FormsModule],
  templateUrl: './patient-form.html',
  styleUrl: './patient-form.css',
})
export class PatientForm implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly patientService = inject(PatientService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  private readonly patientId = signal<number | null>(null);

  protected readonly firstName = signal('');
  protected readonly lastName = signal('');
  protected readonly birthDate = signal('');
  protected readonly gender = signal('');
  protected readonly address = signal('');
  protected readonly phone = signal('');

  protected readonly loading = signal(false);
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal('');

  protected readonly isEditMode = computed(
    () => this.patientId() !== null,
  );

  protected readonly title = computed(() =>
    this.isEditMode() ? 'Edit patient' : 'Add a patient',
  );

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      void this.router.navigate(['/login']);
      return;
    }

    const idValue = this.route.snapshot.paramMap.get('id');

    if (idValue === null) {
      return;
    }

    const id = Number(idValue);

    if (!Number.isInteger(id) || id < 1) {
      this.errorMessage.set('Invalid patient identifier.');
      return;
    }

    this.patientId.set(id);
    this.loadPatient(id);
  }

  save(): void {
    const payload: PatientPayload = {
      firstName: this.firstName().trim(),
      lastName: this.lastName().trim(),
      birthDate: this.birthDate(),
      gender: this.gender(),
      address: this.address().trim() || null,
      phone: this.phone().trim() || null,
    };

    if (
      !payload.firstName ||
      !payload.lastName ||
      !payload.birthDate ||
      !payload.gender
    ) {
      this.errorMessage.set(
        'First name, last name, date of birth and gender are required.',
      );
      return;
    }

    this.errorMessage.set('');
    this.saving.set(true);

    const id = this.patientId();
    const request =
      id === null
        ? this.patientService.create(payload)
        : this.patientService.update(id, payload);

    request.subscribe({
      next: (patient) => {
        void this.router.navigate(['/patients', patient.id]);
      },
      error: (error: HttpErrorResponse) => {
        this.saving.set(false);

        if (error.status === 401 || error.status === 403) {
          this.authService.logout();
          void this.router.navigate(['/login']);
          return;
        }

        this.errorMessage.set(
          error.status === 400
            ? 'Please check the patient information.'
            : 'Unable to save the patient record.',
        );
      },
    });
  }

  cancel(): void {
    const id = this.patientId();

    void this.router.navigate(
      id === null ? ['/patients'] : ['/patients', id],
    );
  }

  logout(): void {
    this.authService.logout();
    void this.router.navigate(['/login']);
  }

  private loadPatient(id: number): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.patientService.getById(id).subscribe({
      next: (patient: Patient) => {
        this.firstName.set(patient.firstName);
        this.lastName.set(patient.lastName);
        this.birthDate.set(patient.birthDate);
        this.gender.set(patient.gender);
        this.address.set(patient.address ?? '');
        this.phone.set(patient.phone ?? '');
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);

        if (error.status === 401 || error.status === 403) {
          this.authService.logout();
          void this.router.navigate(['/login']);
          return;
        }

        this.errorMessage.set(
          error.status === 404
            ? 'Patient not found.'
            : 'Unable to load the patient record.',
        );
      },
    });
  }
}
