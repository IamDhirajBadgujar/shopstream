import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface CartLine {
  productId: string;
  name?: string;
  price?: number;
  qty: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private STORAGE_KEY = 'shopstream_cart_v1';

  private _items$ = new BehaviorSubject<CartLine[]>(this.load());
  items$ = this._items$.asObservable();

  get value(): CartLine[] {
    return this._items$.value;
  }

  private save(items: CartLine[]) {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(items));
    this._items$.next(items);
  }

  private load(): CartLine[] {
    try {
      const raw = localStorage.getItem(this.STORAGE_KEY);
      return raw ? JSON.parse(raw) : [];
    } catch {
      return [];
    }
  }

  add(product: { id?: string, _id?: string, name?: string, price?: number }, qty = 1) {
    const id = (product._id ?? product.id ?? '').toString();
    const items = [...this.value];
    const idx = items.findIndex(i => i.productId === id);
    if (idx >= 0) {
      items[idx].qty += qty;
    } else {
      items.push({ productId: id, name: product.name, price: Number(product.price || 0), qty });
    }
    this.save(items);
  }

  remove(productId: string) {
    const items = this.value.filter(i => i.productId !== productId);
    this.save(items);
  }

  updateQty(productId: string, qty: number) {
    const items = this.value.map(i => i.productId === productId ? {...i, qty: Math.max(0, qty)} : i)
                           .filter(i => i.qty > 0);
    this.save(items);
  }

  clear() {
    this.save([]);
  }

  total() {
    return this.value.reduce((s, it) => s + (it.price || 0) * it.qty, 0);
  }

  count() {
    return this.value.reduce((s, it) => s + it.qty, 0);
  }
}
