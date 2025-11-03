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
import { InteractionType, IPublicClientApplication, PublicClientApplication } from '@azure/msal-browser';
import { importProvidersFrom, inject } from '@angular/core';

export function MSALInstanceFactory(): IPublicClientApplication {
  return new PublicClientApplication({
    auth: {
      clientId: '00000000-0000-0000-0000-000000000000',
      authority: 'https://login.microsoftonline.com/common',
      redirectUri: window.location.origin
    },
    cache: {
      cacheLocation: 'localStorage',
      storeAuthStateInCookie: false
    }
  });
}

export function MSALGuardConfigFactory(): MsalGuardConfiguration {
  return { interactionType: InteractionType.Redirect } as MsalGuardConfiguration;
}

export function MSALInterceptorConfigFactory(): MsalInterceptorConfiguration {
  const protectedResourceMap = new Map<string, Array<string>>();
  protectedResourceMap.set('/api', ['api://azure-api-first/.default']);
  return {
    interactionType: InteractionType.Redirect,
    protectedResourceMap
  } as MsalInterceptorConfiguration;
}

const msalInstance = MSALInstanceFactory();
const guardConfig = MSALGuardConfigFactory();
const interceptorConfig = MSALInterceptorConfigFactory();

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([
      apiInterceptor,
      (req, next) => inject(MsalInterceptor).intercept(req, next)
    ])),
    importProvidersFrom(MsalModule.forRoot(msalInstance, guardConfig, interceptorConfig)),
    { provide: MSAL_INSTANCE, useValue: msalInstance },
    { provide: MSAL_GUARD_CONFIG, useValue: guardConfig },
    { provide: MSAL_INTERCEPTOR_CONFIG, useValue: interceptorConfig },
    MsalService,
    MsalGuard,
    MsalBroadcastService,
    MsalInterceptor
  ]
}).catch(err => console.error(err));
