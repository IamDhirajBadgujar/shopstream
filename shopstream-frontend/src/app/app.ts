import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CartService } from './services/cart.service';
import { AuthService } from './services/auth.service';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, AsyncPipe],
  template: `
    <header style="display:flex;align-items:center;justify-content:space-between;padding:12px;border-bottom:1px solid #eee">
      <div style="font-weight:700">ShopStream</div>

      <nav style="display:flex;align-items:center;gap:12px">
        <a routerLink="/">Shop</a>
        <a routerLink="/cart">Cart ({{ count }})</a>

        <!-- If logged in show username + logout -->
        <ng-container *ngIf="(user$ | async) as username; else showLogin">
          <a routerLink="/profile">{{ username }}</a>
          <button (click)="logout()" style="padding:6px 8px">Logout</button>
        </ng-container>

        <!-- If not logged in -->
        <ng-template #showLogin>
          <a routerLink="/login">Login</a>
        </ng-template>
      </nav>
    </header>

    <main style="padding:12px">
      <router-outlet></router-outlet>
    </main>
  `,
})
export class App {
  count = 0;
  user$ ;

  constructor(private cart: CartService, private auth: AuthService) {
    // Subscribe to cart count
     this.user$ = this.auth.user$;
    this.cart.items$.subscribe(items => {
      this.count = items.reduce((sum, i) => sum + i.qty, 0);
    });
  }

  logout() {
    this.auth.logout();
    location.href = '/'; // redirect after logout
  }
}
