import { Routes } from '@angular/router';

import { Login } from './pages/login/login';
import { PatientDetail } from './pages/patient-detail/patient-detail';
import { PatientForm } from './pages/patient-form/patient-form';
import { PatientList } from './pages/patient-list/patient-list';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: 'login',
    component: Login,
  },
  {
    path: 'patients',
    component: PatientList,
  },
  {
    path: 'patients/new',
    component: PatientForm,
  },
  {
    path: 'patients/:id/edit',
    component: PatientForm,
  },
  {
    path: 'patients/:id',
    component: PatientDetail,
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
