// checkout.component.ts
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { CartService, CartLine } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service'; // 👈 import

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent {
  public address = '';
  public loading = false;
  public success: any = null;
  public error = '';
  public userid: string | null = null;   // 👈 keep userId here

  // expose observable so template doesn't touch cart directly
  public items$!: Observable<CartLine[]>;

  constructor(
    public cart: CartService,
    private http: HttpClient,
    private router: Router,
    private auth: AuthService,           // 👈 inject AuthService
  ) {
    this.items$ = this.cart.items$;

    // subscribe to auth user stream to get userId
    this.auth.user$.subscribe(user => {
      this.userid = user.userId ?? null;
      // console.log('[Checkout] userId from AuthService:', this.userid);
    });
  }

  public get total(): number {
    return (this.cart?.total?.() ?? 0);
  }

  public placeOrder(): void { 
    this.error = '';

    if (!this.userid) {
      this.error = 'You must be logged in to place an order.';
      return;
    }

    const itemsSnapshot: { productId: string; qty: number; price: number,  productName?: string; }[] =
      (this.cart?.value ?? []).map((i: CartLine) => ({
        productId: i.productId,
          productName : i.name,
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
      userId: Number(this.userid),     // 👈 send userId to backend
      items: itemsSnapshot,
      shippingAddress: this.address,
    };
    console.log('Placing order with body:', body);

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

  public log(item: CartLine): void {
    console.log('cart item', item);
  }

  public trackByProductId(index: number, item: CartLine): string {
    return item?.productId ?? index.toString();
  }
}
