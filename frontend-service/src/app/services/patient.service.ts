import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Patient, PatientPayload } from '../models/patient';

@Injectable({
  providedIn: 'root',
})
export class PatientService {
  private readonly http = inject(HttpClient);

  private readonly patientsUrl = 'http://localhost:8080/api/patients';

  getAll(): Observable<Patient[]> {
    return this.http.get<Patient[]>(this.patientsUrl);
  }

getById(id: number): Observable<Patient> {
  return this.http.get<Patient>(`${this.patientsUrl}/${id}`);
}

create(patient: PatientPayload): Observable<Patient> {
  return this.http.post<Patient>(this.patientsUrl, patient);
}

update(id: number, patient: PatientPayload): Observable<Patient> {
  return this.http.put<Patient>(`${this.patientsUrl}/${id}`, patient);
}
}
