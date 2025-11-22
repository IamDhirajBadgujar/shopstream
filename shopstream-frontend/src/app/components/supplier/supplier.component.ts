import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
@Component({
  selector: 'app-supplier',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './supplier.component.html'
})
export class SupplierComponent {
  name = '';
  price = 0;
  stock = 0;
  success = '';
  error = '';

//   constructor(private http: HttpClient, private auth: AuthService) {}
constructor(
    private http: HttpClient, 
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}
  create() {
    this.error = '';
    this.success = '';
    const body = { name: this.name, price: this.price, stock: this.stock };
    this.http.post<any>('http://localhost:8081/api/supplier/products', body).subscribe({
      next: res => {
        this.success = 'Product added';
        this.name = ''; this.price = 0; this.stock = 0;
      },
      error: err => {
        this.error = err?.error || 'Failed to add';
      }
    });
  }
}
