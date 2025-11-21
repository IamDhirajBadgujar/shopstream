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

  // <-- add ActivatedRoute here
  constructor(
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  submit() {
    this.error = '';
    this.loading = true;

    this.auth.login(this.username, this.password).subscribe({
      next: () => {
        this.loading = false;
        // return to requested URL if present
        const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') || '/';
        this.router.navigateByUrl(returnUrl);
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error || 'Login failed';
        console.error(err);
      }
    });
  }
}
