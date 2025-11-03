import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ApiService, AuditEvent, User } from '../../services/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  users: User[] = [];
  audit: AuditEvent[] = [];
  health: string | null = null;

  constructor(private readonly api: ApiService) {}

  ngOnInit(): void {
    this.refresh();
  }

  refresh(): void {
    this.api.getUsers().subscribe(users => (this.users = users));
    this.api.getAudit().subscribe(events => (this.audit = events));
    this.api.getHealth().subscribe(status => (this.health = status.status));
  }
}
