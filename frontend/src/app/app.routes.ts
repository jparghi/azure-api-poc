import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { MsalGuard } from '@azure/msal-angular';

export const routes: Routes = [
  {
    path: '',
    component: DashboardComponent,
    canActivate: [MsalGuard]
  },
  { path: '**', redirectTo: '' }
];
