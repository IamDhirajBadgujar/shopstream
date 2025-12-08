// src/app/components/order-history/order-history.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService, OrderDetails } from '../../services/order.service';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h2 style="margin-bottom: 16px;">My Orders</h2>

    <div *ngIf="loading">Loading...</div>
    <div *ngIf="error" style="color:red">{{ error }}</div>

    <div *ngIf="!loading && !error && orders.length === 0">
      No orders yet.
    </div>

    <div *ngFor="let o of orders"
         style="border:1px solid #ddd;margin-bottom:12px;padding:12px;border-radius:6px;">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:4px;">
        <strong>Order #{{ o.orderId }}</strong>
        <span>{{ o.createdAt | date:'short' }}</span>
      </div>

      <div style="font-size: 13px; color:#555;">
        Ship to: {{ o.shippingAddress }}
      </div>
      <div style="margin-top:4px;font-weight:bold;">
        Total: ₹{{ o.total }}
      </div>

      <table style="width:100%;margin-top:8px;border-collapse:collapse;font-size: 14px;">
        <thead>
          <tr style="border-bottom:1px solid #eee;">
            <th style="text-align:left;padding:4px 0;">Product</th>
            <th style="text-align:right;padding:4px 0;">Qty</th>
            <th style="text-align:right;padding:4px 0;">Price</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let it of o.items" style="border-bottom:1px solid #f5f5f5;">
            <!-- Show productName if present, else fallback to productId -->
            <td style="padding:4px 0;">
              {{ it.productName }}
            </td>
            <td style="text-align:right;padding:4px 0;">
              {{ it.qty }}
            </td>
            <td style="text-align:right;padding:4px 0;">
              ₹{{ it.price }}
            </td>
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
      next: res => {
        this.orders = res || [];
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.error = 'Failed to load orders';
        this.loading = false;
      }
    });
  }
}
