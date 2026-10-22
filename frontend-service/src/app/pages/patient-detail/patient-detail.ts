import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { Patient } from '../../models/patient';
import { PatientService } from '../../services/patient.service';

@Component({
  selector: 'app-patient-detail',
  imports: [RouterLink],
  templateUrl: './patient-detail.html',
  styleUrl: './patient-detail.css',
})
export class PatientDetail implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly patientService = inject(PatientService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly patient = signal<Patient | null>(null);
  protected readonly errorMessage = signal('');
  protected readonly loading = signal(true);

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      void this.router.navigate(['/login']);
      return;
    }

    this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('id'));

      if (!Number.isInteger(id) || id < 1) {
        this.loading.set(false);
        this.errorMessage.set('Invalid patient identifier.');
        return;
      }

      this.loadPatient(id);
    });
  }

  logout(): void {
    this.authService.logout();
    void this.router.navigate(['/login']);
  }

  private loadPatient(id: number): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.patientService.getById(id).subscribe({
      next: (patient) => {
        this.patient.set(patient);
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
            : 'Unable to load patient details.',
        );
      },
    });
  }
}
