import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-list.html',
  styleUrls: ['./product-list.scss'],
})
export class ProductList implements OnInit {
  products: any[] = [];
  loading = true;
  error = '';

  constructor(
    private productService: ProductService,
    private cart: CartService,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.productService.getAllProducts().subscribe({
      next: (res) => { this.products = res || []; this.loading = false; },
      error: (err) => { this.error = 'Failed to load products'; this.loading = false; console.error(err); }
    });
  }

  addToCart(product: any) {
    // require login to add
    if (!this.auth.isAuthenticated()) {
      // optionally show toast; redirect to login and preserve returnUrl
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/' }});
      return;
    }
    this.cart.add(product, 1);
    // show simple confirmation
    alert(`Added "${product.name}" to cart`);
  }
}
