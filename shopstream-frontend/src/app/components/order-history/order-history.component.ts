// src/app/components/order-history/order-history.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderService, OrderDetails } from '../../services/order.service';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule],
  
  templateUrl: './order-history.component.html',
  styleUrls: ['./order-history.component.css'],
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
