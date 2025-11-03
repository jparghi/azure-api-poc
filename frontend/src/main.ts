import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { apiInterceptor } from './app/services/api.interceptor';
import {
  MsalModule,
  MsalService,
  MSAL_INSTANCE,
  MsalGuard,
  MsalGuardConfiguration,
  MSAL_GUARD_CONFIG,
  MSAL_INTERCEPTOR_CONFIG,
  MsalBroadcastService,
  MsalInterceptor,
  MsalInterceptorConfiguration
} from '@azure/msal-angular';
import { InteractionType, PublicClientApplication } from '@azure/msal-browser';
import { importProvidersFrom, inject, Provider } from '@angular/core';
import { environment } from './environments/environment';
import { AZURE_AD_CONFIG, AzureAdConfig } from './app/services/azure-ad-config';

interface AzureAdConfigResponse {
  enabled?: boolean;
  clientId?: string;
  authority?: string;
  apiScope?: string;
}

function buildMsalProviders(config: AzureAdConfig): Provider[] {
  if (!config.enabled) {
    return [];
  }

  const msalInstance = new PublicClientApplication({
    auth: {
      clientId: config.clientId,
      authority: config.authority,
      redirectUri: window.location.origin
    },
    cache: {
      cacheLocation: 'localStorage',
      storeAuthStateInCookie: false
    }
  });

  const guardConfig = { interactionType: InteractionType.Redirect } as MsalGuardConfiguration;

  const protectedResourceMap = new Map<string, Array<string>>();
  const apiRoot = environment.apiBaseUrl.replace(/\/$/, '');
  protectedResourceMap.set(apiRoot, [config.apiScope]);

  const interceptorConfig = {
    interactionType: InteractionType.Redirect,
    protectedResourceMap
  } as MsalInterceptorConfiguration;

  return [
    importProvidersFrom(MsalModule.forRoot(msalInstance, guardConfig, interceptorConfig)),
    { provide: MSAL_INSTANCE, useValue: msalInstance },
    { provide: MSAL_GUARD_CONFIG, useValue: guardConfig },
    { provide: MSAL_INTERCEPTOR_CONFIG, useValue: interceptorConfig },
    MsalService,
    MsalGuard,
    MsalBroadcastService,
    MsalInterceptor
  ];
}

async function loadAzureAdConfig(): Promise<AzureAdConfig> {
  const apiRoot = environment.apiBaseUrl.replace(/\/$/, '');
  try {
    const response = await fetch(`${apiRoot}/config/azure-ad`);
    if (!response.ok) {
      throw new Error(`Failed to load Azure AD config: ${response.status}`);
    }
    const data = (await response.json()) as AzureAdConfigResponse;
    const usable = Boolean(data.enabled) && !!data.clientId && !!data.authority && !!data.apiScope;
    if (!usable) {
      return {
        enabled: false,
        clientId: '',
        authority: '',
        apiScope: ''
      };
    }
    return {
      enabled: true,
      clientId: data.clientId!,
      authority: data.authority!,
      apiScope: data.apiScope!
    };
  } catch (error) {
    console.warn('Azure AD configuration unavailable, continuing without authentication.', error);
    return {
      enabled: false,
      clientId: '',
      authority: '',
      apiScope: ''
    };
  }
}

(async () => {
  const azureAdConfig = await loadAzureAdConfig();
  const interceptors = [apiInterceptor];

  if (azureAdConfig.enabled) {
    interceptors.push((req, next) =>
      inject(MsalInterceptor).intercept(req, {
        handle: incoming => next(incoming)
      })
    );
  }

  const providers: Provider[] = [
    provideRouter(routes),
    provideHttpClient(withInterceptors(interceptors)),
    { provide: AZURE_AD_CONFIG, useValue: azureAdConfig }
  ];

  providers.push(...buildMsalProviders(azureAdConfig));

  bootstrapApplication(AppComponent, {
    providers
  }).catch(err => console.error(err));
})();
