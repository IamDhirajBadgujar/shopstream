// src/app/services/order.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface OrderItemDetails {
  id: number;
  productId: string;
  qty: number;
  price: number;
  productName: string; 
}

export interface OrderDetails {
  orderId: number;
  userId: number;
  shippingAddress: string;
  total: number;
  createdAt: string;
  items: OrderItemDetails[];

}

@Injectable({ providedIn: 'root' })
export class OrderService {
  constructor(private http: HttpClient) {}

  getMyOrders(): Observable<OrderDetails[]> {
    return this.http.get<OrderDetails[]>('/api/orders/my');
  }
}
