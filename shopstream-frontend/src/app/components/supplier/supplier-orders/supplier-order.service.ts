import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SupplierOrderItem } from '../supplier-orders/supplier-orders.component';

@Injectable({ providedIn: 'root' })
export class SupplierOrderService {

  private baseUrl = '/api/orders/supplier-orders';

  constructor(private http: HttpClient) {}

  getSupplierOrders(): Observable<SupplierOrderItem[]> {
    return this.http.get<SupplierOrderItem[]>(this.baseUrl);
  }
}
