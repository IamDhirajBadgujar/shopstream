import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { ActivatedRoute } from '@angular/router';

interface SupplierProduct {
  id: string;
  name: string;
  price: number;
  stock: number;
}

@Component({
  selector: 'app-supplier',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './supplier.component.html',
  styleUrls: ['./supplier.component.css']
})
export class SupplierComponent implements OnInit {
  name = '';
  price = 0;
  stock = 0;
  success = '';
  error = '';

  products: SupplierProduct[] = [];
  loading = false;

  // if not null → editing mode
  editingId: string | null = null;

  constructor(
    private http: HttpClient,
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // you can add role check here if AuthService has hasRole()
    // if (!this.auth.hasRole('SUPPLIER')) {
    //   this.router.navigate(['/login']);
    //   return;
    // }
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.error = '';

    this.http
      .get<SupplierProduct[]>('http://localhost:8081/api/supplier/products/my')
      .subscribe({
        next: res => {
          this.products = res || [];
          this.loading = false;
        },
        error: err => {
          console.error(err);
          this.error = 'Failed to load products';
          this.loading = false;
        }
      });
  }

  // create or update based on editingId
  save(): void {
    this.error = '';
    this.success = '';

    const body = {
      name: this.name,
      price: this.price,
      stock: this.stock
    };

    if (!this.editingId) {
      // create new product
      this.http
        .post<any>('http://localhost:8081/api/supplier/products', body)
        .subscribe({
          next: res => {
            this.success = 'Product added';
            this.resetForm();
            this.loadProducts();
          },
          error: err => {
            console.error(err);
            this.error = err?.error || 'Failed to add';
          }
        });
    } else {
      // update existing product
      this.http
        .put<any>(
          `http://localhost:8081/api/supplier/products/${this.editingId}`,
          body
        )
        .subscribe({
          next: res => {
            this.success = 'Product updated';
            this.resetForm();
            this.loadProducts();
          },
          error: err => {
            console.error(err);
            this.error = err?.error || 'Failed to update';
          }
        });
    }
  }

  startEdit(p: SupplierProduct): void {
    this.editingId = p.id;
    this.name = p.name;
    this.price = p.price;
    this.stock = p.stock;
    this.success = '';
    this.error = '';
  }

  cancelEdit(): void {
    this.resetForm();
  }

  private resetForm(): void {
    this.editingId = null;
    this.name = '';
    this.price = 0;
    this.stock = 0;
  }
}
