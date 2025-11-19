import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CartService } from './services/cart.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink],
  template: `
    <header style="display:flex;align-items:center;justify-content:space-between;padding:12px;border-bottom:1px solid #eee">
      <div style="font-weight:700">ShopStream</div>
      <nav>
        <a routerLink="/" style="margin-right:12px">Shop</a>
        <a routerLink="/cart">Cart (<span>{{ count }}</span>)</a>
      </nav>
    </header>
    <main style="padding:12px">
      <router-outlet></router-outlet>
    </main>
  `
})
export class App {
  count = 0;
  constructor(cart: CartService) {
    cart.items$.subscribe(items => this.count = items.reduce((s,i)=>s+i.qty,0));
  }
}
