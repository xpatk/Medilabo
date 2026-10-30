export interface Note {
  id: string;
  patientId: number;
  text: string;
  createdAt: string;
  modifiedAt: string;
}

export interface CreateNoteRequest {
  text: string;
}
