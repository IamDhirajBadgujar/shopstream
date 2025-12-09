// src/app/guards/supplier.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class SupplierGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(): boolean {
    if (this.auth.hasRole('SUPPLIER')) {
      return true;
    }
    this.router.navigate(['/login']);
    return false;
  }
}
