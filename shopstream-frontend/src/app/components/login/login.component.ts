import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  username = '';
  password = '';
  loading = false;
  error = '';

  constructor(
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  submit() {
    console.log('--- LOGIN SUBMIT START ---');
    console.log('Form Values:', {
      username: this.username,
      password: '(hidden)'
    });

    this.error = '';
    this.loading = true;

    console.log('Calling AuthService.login()...');
    
    this.auth.login(this.username, this.password).subscribe({
      next: (response) => {
        console.log('Login Response Received:', response);
        console.log('AuthService Token:', this.auth.token);
        
        console.log('AuthService User$ current value:', this.auth['_user$'].value);
        console.log('AuthService Roles$ current value:', this.auth['_roles$'].value);

        this.loading = false;

        const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') || '/';
        console.log('Redirecting to:', returnUrl);

        this.router.navigateByUrl(returnUrl);
        console.log('Navigation Done.');
      },
      error: (err: any) => {
        this.loading = false;

        console.log('--- LOGIN ERROR ---');
        console.error('Full HttpErrorResponse:', err);

        if (err?.error instanceof ProgressEvent) {
          console.log('Network / CORS Issue (ProgressEvent)');
        }

        if (typeof err?.error === 'string') {
          console.log('Raw Error Text:', err.error);
        }

        this.error = err?.error || 'Login failed';
        console.log('Displayed Error:', this.error);
      }
    });
  }
}
