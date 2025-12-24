import { Component } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-order-success',
  standalone: true,
  imports: [CommonModule, RouterModule],
  styleUrls: ['./order-success.component.css'],
  template: `
    <div class="success-wrapper">
      <div class="success-card">
        
        <div class="success-icon">✓</div>

        <h2>Order Placed Successfully</h2>

        <p class="success-msg">
          Thank you for your purchase! Your order has been confirmed.
        </p>

        <div class="order-id-box">
          <span>Order ID</span>
          <strong>{{ orderId }}</strong>
        </div>

        <a routerLink="/" class="back-btn">
          Continue Shopping
        </a>

      </div>
    </div>
  `
})
export class OrderSuccessComponent {
  orderId = '';

  constructor(route: ActivatedRoute) {
    this.orderId = route.snapshot.paramMap.get('id') || '';
  }
}
