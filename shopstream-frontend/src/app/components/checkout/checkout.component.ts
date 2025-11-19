// checkout.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { CartService, CartLine } from '../../services/cart.service';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './checkout.component.html',
})
export class CheckoutComponent {
  public address = '';
  public loading = false;
  public success: any = null;
  public error = '';

  // expose observable so template doesn't touch cart directly
  public items$!: Observable<CartLine[]>;

  constructor(public cart: CartService, private http: HttpClient, private router: Router) {
    // assign here (safe because DI is ready in constructor for standalone)
    this.items$ = this.cart.items$;
  }

  // keep as getter — ensure cart.total() is guarded
  public get total(): number {
    return (this.cart?.total?.() ?? 0);
  }

  public placeOrder(): void {
    this.error = '';
    // use latest snapshot from the service value for posting
    const itemsSnapshot: { productId: string; qty: number; price: number }[] =
      (this.cart?.value ?? []).map(i => ({
        productId: i.productId,
        qty: i.qty ?? 0,
        price: i.price ?? 0,
      }));

    if (itemsSnapshot.length === 0) {
      this.error = 'Cart is empty';
      return;
    }

    if (!this.address?.trim()) {
      this.error = 'Please enter a shipping address';
      return;
    }

    this.loading = true;
    const body = {
      items: itemsSnapshot,
      shippingAddress: this.address,
    };

    this.http
      .post<{ orderId: string }>('http://localhost:8080/api/orders', body)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: res => {
          this.success = res;
          this.cart.clear();
          this.router.navigate(['/order-success', res.orderId]);
        },
        error: err => {
          console.error(err);
          this.error = err?.error?.message || 'Failed to place order';
        },
      });
  }

  // helpful debug helper (optional)
  public log(item: CartLine): void {
    console.log('cart item', item);
  }

  // trackBy for ngFor
  public trackByProductId(index: number, item: CartLine): string {
    return item?.productId ?? index.toString();
  }
}
