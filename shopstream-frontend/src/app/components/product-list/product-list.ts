import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';



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

  constructor(private productService: ProductService, private cart: CartService) {}

  ngOnInit(): void {
    this.productService.getAllProducts().subscribe({
      next: (res) => { this.products = res || []; this.loading = false; },
      error: (err) => { this.error = 'Failed to load products'; this.loading = false; console.error(err); }
    });
  }
  
  // Called from template
 addToCart(product: any) {
  this.cart.add(product, 1);
  // optional: show a friendly message
  alert(`Added "${product.name}" to cart`);
}
}
