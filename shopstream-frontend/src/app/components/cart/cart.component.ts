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
  // cart.component.ts

public updateQty(id: string, ev: Event): void {
  const input = ev.target as HTMLInputElement;
  const raw = input.value;

  const parsed = parseInt(raw, 10);
  let qty = Number.isNaN(parsed) ? 0 : Math.max(0, parsed);

  // find the line to get its stock
  const currentLine = this.cart.value.find(i => i.productId === id)
  const maxStock = currentLine?.stock ?? Number.MAX_SAFE_INTEGER;

  console.log(`[Cart] updateQty for ${id}: requested=${raw} parsed=${qty} maxStock=${maxStock}`);


  if (qty > maxStock) {
    // clamp to available stock
    qty = maxStock;
    // update the input visually
    input.value = String(qty);
    // simple feedback – you can replace with toast/snackbar
    alert(`Only ${maxStock} units available in stock.`);
  }

  if (qty <= 0) {
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
