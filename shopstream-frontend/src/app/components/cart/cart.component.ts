import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { CartService, CartLine } from '../../services/cart.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cart.component.html',
})
export class CartComponent implements OnInit {
  items$!: Observable<CartLine[]>; // set in ngOnInit

  constructor(private cart: CartService, private router: Router) {}

  ngOnInit(): void {
    // safer to assign the observable after injection lifecycle
    this.items$ = this.cart.items$;
  }

  // remove item by id
  public remove(id: string): void {
    this.cart.remove(id);
  }

  // update quantity from an <input> event
  public updateQty(id: string, ev: Event): void {
    const raw = (ev.target as HTMLInputElement).value;
    // parse integer quantity and ensure a non-negative integer
    const parsed = parseInt(raw, 10);
    const qty = Number.isNaN(parsed) ? 0 : Math.max(0, parsed);

    if (qty <= 0) {
      // decide behaviour: remove item when quantity is zero
      this.cart.remove(id);
    } else {
      this.cart.updateQty(id, qty);
    }
  }

  // navigate to checkout route
  public checkout(): void {
    this.router.navigate(['/checkout']);
  }

  // synchronous total (assumes cart.total() returns a number)
  public total(): number {
    return this.cart.total();
  }

  trackByProductId(index: number, item: CartLine): string {
  // guard for missing productId
  return item?.productId ?? index.toString();
}

log(item: CartLine): void {
  console.log('cart item', item);
}


}
