import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-order-success',
  standalone: true,
  imports: [CommonModule],
  template: `<div style="padding:20px"><h2>Order placed</h2><p>Order id: {{ orderId }}</p><a routerLink="/">Back to shop</a></div>`
})
export class OrderSuccessComponent {
  orderId = '';
  constructor(route: ActivatedRoute) {
    this.orderId = route.snapshot.paramMap.get('id') || '';
  }
}
