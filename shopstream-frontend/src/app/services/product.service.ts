import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  // InventoryService exposes products on port 8081 as instructed earlier
  private BASE_URL = 'http://localhost:8081/api/products';

  constructor(private http: HttpClient) {}

  getAllProducts(): Observable<any[]> {
    return this.http.get<any[]>(this.BASE_URL);
  }
   searchProducts(filters: any) {
    return this.http.get<Product[]>('/api/products/search', {
      params: filters
    });
  }
}

export interface Product {
  id: string;
  name: string;
  price: number;
  stock: number;
  category: string;
}

