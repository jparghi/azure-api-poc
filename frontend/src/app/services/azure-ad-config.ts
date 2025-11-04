import { InjectionToken } from '@angular/core';

export interface AzureAdConfig {
  enabled: boolean;
  clientId: string;
  authority: string;
  apiScope: string;
}

export const AZURE_AD_CONFIG = new InjectionToken<AzureAdConfig>('AZURE_AD_CONFIG');

