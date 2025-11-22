import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  username = '';
  email = '';
  password = '';
  loading = false;
  error = '';
 beSupplier: boolean = false; 


  constructor(private auth: AuthService, private router: Router) {}

  submit() {
    this.error = '';
    this.loading = true;
    this.auth.register(this.username, this.email, this.password, this.beSupplier).subscribe({
      next: () => {
        this.loading = false;
        // registered and auto-logged in (AuthService saves token)
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error || 'Registration failed';
        console.error(err);
      }
    });
  }
}
