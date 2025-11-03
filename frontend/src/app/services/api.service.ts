import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface User {
  id: number;
  username: string;
  email: string;
  role: string;
}

export interface AuditEvent {
  timestamp: string;
  method: string;
  path: string;
  user: string;
  status: number;
}

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly apiRoot = environment.apiBaseUrl;
  private readonly baseUrl = `${this.apiRoot}/v1`;

  constructor(private http: HttpClient) {}

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.baseUrl}/users`);
  }

  getAudit(): Observable<AuditEvent[]> {
    return this.http.get<AuditEvent[]>(`${this.baseUrl}/audit`);
  }

  getHealth(): Observable<{ status: string }> {
    const actuatorBase = this.apiRoot.replace(/\/api$/, '');
    return this.http.get<{ status: string }>(`${actuatorBase}/actuator/health`);
  }
}
