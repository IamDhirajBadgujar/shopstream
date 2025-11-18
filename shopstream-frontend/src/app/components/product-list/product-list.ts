import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-list.html',
})
export class ProductListComponent implements OnInit {
  products: any[] = [];
  loading = true;
  error = '';

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.productService.getAllProducts().subscribe({
      next: (res) => { this.products = res || []; this.loading = false; },
      error: (err) => { this.error = 'Failed to load products'; this.loading = false; console.error(err); }
    });
  }

  // Called from template
  addToCart(product: any) {
    // temporary: log to console and show a quick browser alert
    console.log('Add to cart:', product);
    alert(`Added "${product.name}" to cart (demo)`);
    // later: integrate a CartService to store items
  }
}
