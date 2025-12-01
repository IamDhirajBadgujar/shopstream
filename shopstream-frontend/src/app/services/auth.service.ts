// auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, from, of } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';
import { CartService } from './cart.service';
import { isBrowser } from '../utils/browser';
import { throwError } from 'rxjs';

import { tap, catchError, finalize } from 'rxjs/operators';

export interface AuthUser {
  userId: string | null;
  username?: string | null;
}

interface LoginResp {
  token: string;
  userid?: number;                 // 👈 match backend
  username?: string | null;
}


interface RegisterResp {
  token: string;
  userId?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  // internal token store (in-memory)
  private _token: string | null = null;
  private userid: number | null = null;
  // public observables
  private _user$ = new BehaviorSubject<AuthUser>({ userId: null, username: null });
  public user$ = this._user$.asObservable();

  private _roles$ = new BehaviorSubject<string[]>([]);
  public roles$ = this._roles$.asObservable();

  constructor(private http: HttpClient, private cart: CartService) {
    // NOTE: do NOT touch localStorage here (avoids SSR errors).
    // Call restoreSession() from AppComponent.ngOnInit() in the browser to initialize.
  }

  /**
   * Safe token getter used by interceptors or components.
   * Prefer the in-memory token, fallback to localStorage if present (browser only).
   */
  get token(): string | null {
    if (this._token) return this._token;
    if (isBrowser()) {
      const t = localStorage.getItem('auth_token');
      if (t) this._token = t;
      return t;
    }
    return null;
  }

  /** simple boolean helper */
  public isAuthenticated(): boolean {
    return !!this.token;
  }

  /** Call this ONCE on browser bootstrap (e.g. AppComponent.ngOnInit) */
  public restoreSession(): void {
    if (!isBrowser()) return;

    const token = localStorage.getItem('auth_token');
    if (!token) return;

    // set in-memory token
    this._token = token;

    const claims = this.decodeJwtPayload(token);
    const userId = this.getUserIdFromClaims(claims);
    const username = claims?.sub ?? null;
    const roles = this.getRolesFromClaims(claims);

    this._user$.next({ userId, username });
    this._roles$.next(roles);

    if (userId) {
      // fire-and-forget: initialize cart for user (merge guest cart)
      this.cart.handleLogin(userId).catch(err => console.warn('cart.init failed', err));
    }
  }

  // ---------- Auth flows ----------

  // login: returns Observable that completes after cart merge is done
login(username: string, password: string): Observable<any> {
  const url = '/api/auth/login'; // use proxy during dev; use absolute if not

  console.log('%c[AuthService] LOGIN start', 'color:green;font-weight:bold;');
  console.log('[AuthService] URL:', url);
  console.log('[AuthService] Payload:', { username, password: '(hidden)' });

  return this.http.post<LoginResp>(url, { username, password  }).pipe(
    tap({
      next: (resp) => {
        console.log('%c[AuthService] HTTP SUCCESS', 'color:blue;font-weight:bold;', resp);
        console.log('[AuthService] Response UserId:', resp.userid);
      },
      error: (err) => {
        console.error('%c[AuthService] HTTP ERROR (tap)', 'color:red;font-weight:bold;', err);
      }
    }),
    switchMap(resp => {
      // existing logic: check token, persist, emit user/roles, merge cart...
      if (!resp || !resp.token) {
        console.warn('[AuthService] No token in response', resp);
        return of({ ok: false, error: 'No token in response' });
      }

      this._token = resp.token;
      this.userid = resp.userid ? (resp.userid) : null;
      console.log('[AuthService] Derived userId:', this.userid);

      try { localStorage.setItem('auth_token', resp.token); } catch(e) {}
     
      const claims = this.decodeJwtPayload(resp.token);
      const userId = resp.userid ? String(resp.userid) : this.getUserIdFromClaims(claims);
      console.log('[AuthService] Derived userId:',this.userid );
      const usernameFromToken = claims?.sub ?? resp.username ?? null;
      const roles = this.getRolesFromClaims(claims);

      this._user$.next({ userId, username: usernameFromToken });
      this._roles$.next(roles);

      if (userId) {
        return from(this.cart.handleLogin(userId)).pipe(
          switchMap(() => of({ ok: true, token: resp.token, userId, username: usernameFromToken, roles }))
        );
      } else {
        return of({ ok: true, token: resp.token, username: usernameFromToken, roles });
      }
    }),
    catchError((err: any) => {
      // helpful logging + rethrow so component's error handler executes
      console.error('%c[AuthService] catchError -> rethrow', 'color:red;', err);
      // If network/CORS: err.error instanceof ProgressEvent
      return throwError(() => err);
    }),
    finalize(() => console.log('%c[AuthService] LOGIN stream complete', 'color:purple;'))
  );
}

  // register: similar to login, persists token and merges cart
  register(username: string, email: string, password: string, beSupplier: boolean = false) {
    return this.http.post<RegisterResp>('/api/auth/register', {
      username,
      email,
      password,
      roles: beSupplier ? ['ROLE_SUPPLIER'] : []
    }).pipe(
      switchMap(resp => {
        if (!resp || !resp.token) {
          return of({ ok: false, error: 'No token in register response' });
        }

        // persist token (browser only) and update in-memory token
        this._token = resp.token;
        if (isBrowser()) localStorage.setItem('auth_token', resp.token);

        // derive userId and claims
        let userId = resp.userId ? String(resp.userId) : null;
        const claims = this.decodeJwtPayload(resp.token);
        if (!userId) userId = this.getUserIdFromClaims(claims);
        const usernameFromToken = claims?.sub ?? username;
        const roles = this.getRolesFromClaims(claims);

        // emit user & roles
        this._user$.next({ userId, username: usernameFromToken });
        this._roles$.next(roles);

        // merge guest cart to server and initialize cart for user
        if (userId) {
          return from(this.cart.handleLogin(userId)).pipe(map(() => ({ ok: true })));
        } else {
          return of({ ok: true });
        }
      })
    );
  }

  // logout: clear token + emit defaults
  logout(): void {
    this._token = null;
    if (isBrowser()) localStorage.removeItem('auth_token');
    this._user$.next({ userId: null, username: null });
    this._roles$.next([]);
    this.cart.handleLogout();
  }

  // ---------- helpers for JWT parsing ----------
  private decodeJwtPayload(token: string): any | null {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) return null;
      const payload = atob(parts[1].replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(decodeURIComponent(escape(payload)));
    } catch {
      return null;
    }
  }

  private getUserIdFromClaims(claims: any): string | null {
    if (!claims) return null;
    if (claims.userId) return String(claims.userId);
    if (claims.uid) return String(claims.uid);
    if (claims.sub) return String(claims.sub);
    return null;
  }

  private getRolesFromClaims(claims: any): string[] {
    if (!claims) return [];
    const r = claims.roles ?? claims.role ?? [];
    if (Array.isArray(r)) return r.map(String);
    if (typeof r === 'string') return [r];
    return [];
  }
}
