import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { Note } from '../../models/note';
import { Patient } from '../../models/patient';
import { NoteService } from '../../services/note.service';
import { PatientService } from '../../services/patient.service';

import { Assessment } from '../../models/assessment';
import { AssessmentService } from '../../services/assessment.service';

@Component({
  selector: 'app-patient-detail',
  imports: [DatePipe, FormsModule, RouterLink],
  templateUrl: './patient-detail.html',
  styleUrl: './patient-detail.css',
})
export class PatientDetail implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly noteService = inject(NoteService);
  private readonly patientService = inject(PatientService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly assessmentService = inject(AssessmentService);

  protected readonly patient = signal<Patient | null>(null);
  protected readonly notes = signal<Note[]>([]);

  protected readonly errorMessage = signal('');
  protected readonly notesErrorMessage = signal('');
  protected readonly noteFormErrorMessage = signal('');

  protected readonly loading = signal(true);
  protected readonly notesLoading = signal(false);
  protected readonly savingNote = signal(false);

  protected readonly newNoteText = signal('');

  protected readonly assessment = signal<Assessment | null>(null);
  protected readonly assessmentLoading = signal(false);
  protected readonly assessmentErrorMessage = signal('');

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
      this.loadNotes(id);
      this.loadAssessment(id);
    });
  }

  logout(): void {
    this.authService.logout();
    void this.router.navigate(['/login']);
  }

  createNote(): void {
    const patient = this.patient();
    const text = this.newNoteText();

    this.noteFormErrorMessage.set('');

    if (!patient) {
      this.noteFormErrorMessage.set('Patient details are not available.');
      return;
    }

    if (!text.trim()) {
      this.noteFormErrorMessage.set('Note text is required.');
      return;
    }

    this.savingNote.set(true);

    this.noteService.createForPatient(patient.id, text).subscribe({
next: (note) => {
  this.notes.update((notes) => [note, ...notes]);
  this.loadAssessment(patient.id);
  this.newNoteText.set('');
  this.savingNote.set(false);
},
      error: (error: HttpErrorResponse) => {
        this.savingNote.set(false);

        if (this.handleAuthenticationError(error)) {
          return;
        }

        this.noteFormErrorMessage.set('Unable to save the note.');
      },
    });
  }

protected riskClass(): string {
  const riskLevel = this.assessment()?.riskLevel;

  switch (riskLevel) {
    case 'None':
      return 'risk-none';
    case 'Borderline':
      return 'risk-borderline';
    case 'In Danger':
      return 'risk-danger';
    case 'Early onset':
      return 'risk-early-onset';
    default:
      return '';
  }
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

        if (this.handleAuthenticationError(error)) {
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

  private loadNotes(patientId: number): void {
    this.notes.set([]);
    this.notesLoading.set(true);
    this.notesErrorMessage.set('');

    this.noteService.getForPatient(patientId).subscribe({
      next: (notes) => {
        this.notes.set(notes);
        this.notesLoading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.notesLoading.set(false);

        if (this.handleAuthenticationError(error)) {
          return;
        }

        this.notesErrorMessage.set('Unable to load patient notes.');
      },
    });
  }

private loadAssessment(patientId: number): void {
  this.assessment.set(null);
  this.assessmentLoading.set(true);
  this.assessmentErrorMessage.set('');

  this.assessmentService.getForPatient(patientId).subscribe({
    next: (assessment) => {
      this.assessment.set(assessment);
      this.assessmentLoading.set(false);
    },
    error: (error: HttpErrorResponse) => {
      this.assessmentLoading.set(false);

      if (this.handleAuthenticationError(error)) {
        return;
      }

      this.assessmentErrorMessage.set(
        'Unable to calculate the diabetes risk assessment.',
      );
    },
  });
}

  private handleAuthenticationError(error: HttpErrorResponse): boolean {
    if (error.status !== 401 && error.status !== 403) {
      return false;
    }

    this.authService.logout();
    void this.router.navigate(['/login']);

    return true;
  }
}
