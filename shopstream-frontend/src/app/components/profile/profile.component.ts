import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, AsyncPipe],
  template: `
    <div style="max-width:640px;margin:18px auto;padding:18px;border:1px solid #ddd;border-radius:6px">
      <h2>Your Profile</h2>

      <div *ngIf="user$ | async as username; else anon">
        <p><strong>Username:</strong> {{ username }}</p>
        <p><button (click)="logout()">Logout</button></p>
      </div>

      <ng-template #anon>
        <p>You are not logged in. <a routerLink="/login">Login</a></p>
      </ng-template>
    </div>
  `
})
export class ProfileComponent {
  user$ ;
  constructor(private auth: AuthService, private router: Router) {
    this.user$ = this.auth.user$;
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
