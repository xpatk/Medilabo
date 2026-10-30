import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { CreateNoteRequest, Note } from '../models/note';

@Injectable({
  providedIn: 'root',
})
export class NoteService {
  private readonly http = inject(HttpClient);

  private readonly notesUrl = 'http://localhost:8080/api/notes';

  getForPatient(patientId: number): Observable<Note[]> {
    return this.http.get<Note[]>(
      `${this.notesUrl}/patients/${patientId}`,
    );
  }

  createForPatient(
    patientId: number,
    text: string,
  ): Observable<Note> {
    const request: CreateNoteRequest = { text };

    return this.http.post<Note>(
      `${this.notesUrl}/patients/${patientId}`,
      request,
    );
  }
}
