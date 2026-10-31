import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Assessment } from '../models/assessment';

@Injectable({
  providedIn: 'root',
})
export class AssessmentService {
  private readonly http = inject(HttpClient);

  private readonly assessmentsUrl =
    'http://localhost:8080/api/assessments';

  getForPatient(patientId: number): Observable<Assessment> {
    return this.http.get<Assessment>(
      `${this.assessmentsUrl}/patients/${patientId}`,
    );
  }
}
