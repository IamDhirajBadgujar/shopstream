import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-product-list',
  standalone: true,
   imports: [CommonModule, FormsModule],  // ✅ ADD THIS
  templateUrl: './product-list.html',
  styleUrls: ['./product-list.scss'],
})
export class ProductList implements OnInit {
  filters = {
    q: '',
    minPrice: '',
    maxPrice: '',
    inStock: false,
    sort: 'latest'
  };
 
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
  //   ngOnInit(): void {
  //   this.loadAll();
  // }

  loadAll(): void {
    this.loading = true;
    this.productService.getAllProducts().subscribe({
      next: res => {
        this.products = res || [];
        this.loading = false;
      },
      error: err => {
        this.error = 'Failed to load products';
        this.loading = false;
        console.error(err);
      }
    });
  }
search(): void {
  const params: any = {};

  if (this.filters.q?.trim()) {
    params.q = this.filters.q.trim();
  }

  if (this.filters.minPrice !== '') {
    params.minPrice = Number(this.filters.minPrice);
  }

  if (this.filters.maxPrice !== '') {
    params.maxPrice = Number(this.filters.maxPrice);
  }

  if (this.filters.inStock) {
    params.inStock = true;
  }

  if (this.filters.sort) {
    params.sort = this.filters.sort;
  }

  this.loading = true;

  // this.productService.searchProducts(params).subscribe({
  //   next: res => {
  //     this.products = res || [];
  //     this.loading = false;
  //   },
  //   error: err => {
  //     console.error('Search error:', err);
  //     this.error = 'Search failed';
  //     this.loading = false;
  //   }
  // });
  this.productService.searchProducts(params).subscribe({
  next: res => {
    console.log('SEARCH RESPONSE:', res);
    console.log('COUNT:', res?.length);

    this.products = res || [];

    console.log('UPDATED PRODUCTS:', this.products);

    this.loading = false;
  },
  error: err => {
    console.error('Search error:', err);
    this.loading = false;
  }
});

}


  reset(): void {
    this.filters = {
      q: '',
      minPrice: '',
      maxPrice: '',
      inStock: false,
      sort: 'latest'
    };
    this.loadAll();
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
