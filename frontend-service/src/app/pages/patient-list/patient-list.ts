import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { Patient } from '../../models/patient';
import { PatientService } from '../../services/patient.service';

@Component({
  selector: 'app-patient-list',
  imports: [RouterLink],
  templateUrl: './patient-list.html',
  styleUrl: './patient-list.css',
})
export class PatientList implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly patientService = inject(PatientService);
  private readonly router = inject(Router);

  protected readonly username = this.authService.username;
  protected readonly patients = signal<Patient[]>([]);
  protected readonly errorMessage = signal('');
  protected readonly loading = signal(false);

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      void this.router.navigate(['/login']);
      return;
    }

    this.loadPatients();
  }

  logout(): void {
    this.authService.logout();
    this.patients.set([]);
    void this.router.navigate(['/login']);
  }

  private loadPatients(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.patientService.getAll().subscribe({
      next: (patients) => {
        this.patients.set(patients);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);

        if (error.status === 401 || error.status === 403) {
          this.authService.logout();
          void this.router.navigate(['/login']);
          return;
        }

        this.errorMessage.set('Unable to load patients.');
      },
    });
  }
}
