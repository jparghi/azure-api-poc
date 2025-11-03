import { CanActivateFn } from '@angular/router';
import { inject } from '@angular/core';
import { AZURE_AD_CONFIG } from './azure-ad-config';
import { MsalGuard } from '@azure/msal-angular';

export const authGuard: CanActivateFn = (route, state) => {
  const config = inject(AZURE_AD_CONFIG);
  if (!config.enabled) {
    return true;
  }
  return inject(MsalGuard).canActivate(route, state);
};

