// src/app/components/order-history/order-history.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService, OrderDetails } from '../../services/order.service';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h2>My Orders</h2>

    <div *ngIf="loading">Loading...</div>
    <div *ngIf="error" style="color:red">{{ error }}</div>

    <div *ngIf="!loading && !error && orders.length === 0">
      No orders yet.
    </div>

    <div *ngFor="let o of orders" style="border:1px solid #ddd;margin-bottom:12px;padding:8px;border-radius:6px;">
      <div><strong>Order #{{ o.orderId }}</strong></div>
      <div>Date: {{ o.createdAt | date:'short' }}</div>
      <div>Ship to: {{ o.shippingAddress }}</div>
      <div>Total: ₹{{ o.total }}</div>

      <table style="width:100%;margin-top:8px;border-collapse:collapse;">
        <thead>
          <tr>
            <th style="text-align:left;">Product</th>
            <th style="text-align:right;">Qty</th>
            <th style="text-align:right;">Price</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let it of o.items">
            <td>{{ it.productId }}</td>
            <td style="text-align:right;">{{ it.qty }}</td>
            <td style="text-align:right;">₹{{ it.price }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  `
})
export class OrderHistoryComponent implements OnInit {
  orders: OrderDetails[] = [];
  loading = false;
  error = '';

  constructor(private orderService: OrderService) {}

  ngOnInit() {
    this.loading = true;
    this.orderService.getMyOrders().subscribe({
      next: res => { this.orders = res || []; this.loading = false; },
      error: err => {
        console.error(err);
        this.error = 'Failed to load orders';
        this.loading = false;
      }
    });
  }
}
