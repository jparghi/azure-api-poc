import { HttpInterceptorFn } from '@angular/common/http';

export const apiInterceptor: HttpInterceptorFn = (req, next) => {
  const cloned = req.clone({
    setHeaders: {
      'X-APIM-Base-Url': window.localStorage.getItem('apimBaseUrl') ?? ''
    }
  });
  return next(cloned);
};
