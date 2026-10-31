export type RiskLevel =
  | 'None'
  | 'Borderline'
  | 'In Danger'
  | 'Early onset';

export interface Assessment {
  patientId: number;
  riskLevel: RiskLevel;
}
