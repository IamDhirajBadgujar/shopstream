// cart.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject, lastValueFrom, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';

export interface CartLine {
  productId: string;
  name?: string;
  price?: number;
  qty: number;
  stock?: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  // base key; actual key becomes `shopstream_cart_v1_guest` or `shopstream_cart_v1_user_<userId>`
  private STORAGE_KEY_BASE = 'shopstream_cart_v1';
  private userId: string | null = null; // set when user logs in

  private _items$ = new BehaviorSubject<CartLine[]>(this.load());
  items$ = this._items$.asObservable();

  constructor(private http: HttpClient) {}

  // -----------------------
  // Storage helpers
  // -----------------------
  private storageKey(): string {
    return this.userId ? `${this.STORAGE_KEY_BASE}_user_${this.userId}` : `${this.STORAGE_KEY_BASE}_guest`;
  }

  private save(items: CartLine[]) {
    localStorage.setItem(this.storageKey(), JSON.stringify(items));
    this._items$.next(items);
  }

  private load(): CartLine[] {
    try {
      const raw = localStorage.getItem(this.storageKey());
      return raw ? JSON.parse(raw) : [];
    } catch {
      return [];
    }
  }

  // call this on app startup (optional) to rehydrate from appropriate key
  public init(userId?: string | null) {
    this.userId = userId ?? null;
    // re-read items from the correct key and emit
    this._items$.next(this.load());
  }
 get value(): CartLine[] {
  return this._items$.value;
}


  // -----------------------
  // Public cart operations (local + server sync when logged in)
  // -----------------------
  // Add item (local update + server call if logged in)
  add(
  product: { id?: string; _id?: string; name?: string; price?: number; stock?: number },
  qty = 1
) {
  const id = (product._id ?? product.id ?? '').toString();
  const items = [...this.value];
  const idx = items.findIndex((i) => i.productId === id);

  const available = product.stock ?? Number.MAX_SAFE_INTEGER; // fallback if no stock info

  if (idx >= 0) {
    // respect stock when increasing
    const current = items[idx];
    const newQty = Math.min(current.qty + qty, available);
    items[idx] = { ...current, qty: newQty, stock: available };
  } else {
    items.push({
      productId: id,
      name: product.name,
      price: Number(product.price || 0),
      qty: Math.min(qty, available),
      stock: available,
    });
  }

  this.save(items); // <- CartService.save()
}


  async remove(productId: string) {
    const items = this._items$.value.filter(i => i.productId !== productId);
    this.save(items);

    if (this.isLoggedIn()) {
      try {
        const updated: any = await lastValueFrom(this.http.delete(`/api/cart/items/${productId}`));
        const mapped = this.mapServerCartToLocal(updated);
        this.save(mapped);
      } catch (err) {
        console.warn('Cart sync remove failed', err);
      }
    }
  }

  async updateQty(productId: string, qty: number) {
    const items = this._items$.value.map(i => i.productId === productId ? {...i, qty: Math.max(0, qty)} : i)
                                    .filter(i => i.qty > 0);
    this.save(items);

    if (this.isLoggedIn()) {
      try {
        const updated: any = await lastValueFrom(this.http.put(`/api/cart/items/${productId}`, { quantity: qty }));
        const mapped = this.mapServerCartToLocal(updated);
        this.save(mapped);
      } catch (err) {
        console.warn('Cart sync update failed', err);
      }
    }
  }

  // clear local and server cart
  async clear() {
    this.save([]);
    if (this.isLoggedIn()) {
      try {
        await lastValueFrom(this.http.delete('/api/cart'));
      } catch (err) {
        console.warn('Cart clear failed on server', err);
      }
    }
  }

  total() {
    return this._items$.value.reduce((s, it) => s + (it.price || 0) * it.qty, 0);
  }

  count() {
    return this._items$.value.reduce((s, it) => s + it.qty, 0);
  }

  // -----------------------
  // Login / Logout / Merge flows
  // -----------------------

  // call this when user logs in (pass the string userId from your AuthService)
  // it will:
  // 1) merge existing guest cart into server (POST /api/cart/merge)
  // 2) load server cart and save it under user key
  public async handleLogin(userId: string) {
    const prevGuest = this.getGuestItems(); // snapshot of guest items (if any)
    this.userId = userId;
    // ensure the in-memory and storage key switch: if new user key has items, load them; otherwise we will merge
    const existingForUser = this.load();
    if (existingForUser && existingForUser.length) {
      // user already had local cart on this device: prefer server sync to be safe
      try {
        const updated: any = await lastValueFrom(this.http.post('/api/cart/merge', this.toMergePayload(existingForUser)));
        const mapped = this.mapServerCartToLocal(updated);
        this.save(mapped);
        return;
      } catch (err) {
        console.warn('Failed to sync existing user-local cart with server, keeping local copy', err);
        this.save(existingForUser);
        return;
      }
    }

    // If no local user cart, merge guest into server (common flow)
    if (prevGuest && prevGuest.length) {
      try {
        const updated: any = await lastValueFrom(this.http.post('/api/cart/merge', this.toMergePayload(prevGuest)));
        const mapped = this.mapServerCartToLocal(updated);
        this.save(mapped);
        // clear guest key (guest cart moved to user-specific storage)
        localStorage.removeItem(`${this.STORAGE_KEY_BASE}_guest`);
        return;
      } catch (err) {
        console.warn('Merge guest cart failed, falling back to loading server cart', err);
      }
    }

    // as a fallback, try to load server cart
    try {
      const serverCart: any = await lastValueFrom(this.http.get('/api/cart'));
      const mapped = this.mapServerCartToLocal(serverCart);
      this.save(mapped);
    } catch (err) {
      console.warn('Failed to load server cart after login, keeping empty/local state', err);
      this.save([]);
    }
  }

  // call on logout: switch to guest key (keep user cart persisted under user key)
  public handleLogout() {
    this.userId = null;
    // load guest cart or empty
    this._items$.next(this.load());
  }

  // -----------------------
  // Utilities
  // -----------------------
  private isLoggedIn(): boolean {
    return !!this.userId;
  }

  // snapshot of guest items (reads guest key directly)
  private getGuestItems(): CartLine[] {
    try {
      const raw = localStorage.getItem(`${this.STORAGE_KEY_BASE}_guest`);
      return raw ? JSON.parse(raw) : [];
    } catch {
      return [];
    }
  }

  // map server cart response -> CartLine[] (adjust shape to your server response)
  private mapServerCartToLocal(serverCart: any): CartLine[] {
    if (!serverCart) return [];
    // expected serverCart { items: [{ productId, quantity, productName?, price? }, ...] }
    if (Array.isArray(serverCart)) {
      // some endpoints might return array of items directly
      return serverCart.map((it: any) => ({
        productId: String(it.productId ?? it.product_id ?? it.id),
        qty: Number(it.quantity ?? it.qty ?? 0),
        name: it.name ?? it.productName,
        price: it.price ?? 0
      }));
    }
    const items = serverCart.items ?? serverCart.cartItems ?? [];
    return items.map((it: any) => ({
      productId: String(it.productId ?? it.product_id ?? it.id),
      qty: Number(it.quantity ?? it.qty ?? 0),
      name: it.name ?? it.productName,
      price: it.price ?? 0
    }));
  }

  // convert CartLine[] to merge payload expected by backend
  private toMergePayload(items: CartLine[]) {
    return items.map(i => ({ productId: i.productId, quantity: i.qty }));
  }
}
