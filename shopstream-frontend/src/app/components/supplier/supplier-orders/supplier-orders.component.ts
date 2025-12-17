import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SupplierOrderService } from './supplier-order.service';

/* ✅ Interface must be OUTSIDE the component */
export interface SupplierOrderItem {
  orderId: number;
  userId: string;       // or buyerName
  address: string;
  productId: string;
  productName: string;
  qty: number;
  price: number;
}

@Component({
  selector: 'app-supplier-orders',
  standalone: true,               // ✅ if using standalone
  imports: [CommonModule],        // ✅ required for *ngIf, *ngFor
  templateUrl: './supplier-orders.component.html'
})
export class SupplierOrdersComponent implements OnInit {

  orders: SupplierOrderItem[] = [];
  loading = true;
  error = '';

  constructor(private orderService: SupplierOrderService) {}

  ngOnInit(): void {
    this.orderService.getSupplierOrders().subscribe({
      next: data => {
        this.orders = data;
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
