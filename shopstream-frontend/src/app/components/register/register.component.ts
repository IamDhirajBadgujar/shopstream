import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <form (ngSubmit)="submit()">
      <input [(ngModel)]="username" name="username" placeholder="username"/><br/>
       <input [(ngModel)]="email" name="email" placeholder="email"/><br/>
      <input [(ngModel)]="password" name="password" type="password" placeholder="password"/><br/>
      <button type="submit">Login</button>
    </form>
  `
})
export class RegisterComponent {
  username = '';
  password = '';
  email = '';
  constructor(private auth: AuthService, private router: Router) {}
  submit() {
    this.auth.register(this.username,this.email, this.password).subscribe({
      next: () => this.router.navigate(['/']),
      error: err => alert('register failed')
    });
  }
}
