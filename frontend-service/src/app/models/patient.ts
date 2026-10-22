export interface PatientPayload {
  firstName: string;
  lastName: string;
  birthDate: string;
  gender: string;
  address: string | null;
  phone: string | null;
}

export interface Patient extends PatientPayload {
  id: number;
}
