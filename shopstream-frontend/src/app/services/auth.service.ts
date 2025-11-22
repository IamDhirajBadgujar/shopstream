import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, tap } from 'rxjs';

interface AuthResp { token: string; username: string; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private KEY = 'shopstream_jwt';

  // start with null, populate later safely
  private _user$ = new BehaviorSubject<string | null>(null);
  user$ = this._user$.asObservable();

  constructor(private http: HttpClient) {
    // Delay reading localStorage until runtime environment is available
    // (protects SSR / build-time where localStorage is not defined)
    try {
      const username = this.getUsernameFromToken();
      this._user$.next(username);
    } catch (e) {
      // ignore - running in non-browser environment
      this._user$.next(null);
    }
  }

  register(username: string, email:string, password:string, beSupplier:boolean) {
    return this.http.post<AuthResp>('http://localhost:8080/api/auth/register', { username, email, password , beSupplier })
      .pipe(tap(res => this.saveToken(res.token)));
  }

  login(username: string, password: string) {
    return this.http.post<AuthResp>('http://localhost:8080/api/auth/login', { username, password })
      .pipe(tap(res => this.saveToken(res.token)));
  }

  private _roles$ = new BehaviorSubject<string[]>([]);
roles$ = this._roles$.asObservable();

private saveToken(token: string) {
  if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
    localStorage.setItem(this.KEY, token);
  }
  const username = this.getUsernameFromToken();
  this._user$.next(username);

  // parse roles
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const roles = payload.roles || [];
    this._roles$.next(Array.isArray(roles) ? roles : [roles]);
  } catch {
    this._roles$.next([]);
  }
}

isSupplier() {
  return this._roles$.value.includes('ROLE_SUPPLIER');
}


 logout() {
  if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
    localStorage.removeItem(this.KEY);
  }
  this._user$.next(null);
}


  get token() {
    if (typeof window === 'undefined' || typeof localStorage === 'undefined') return null;
    return localStorage.getItem(this.KEY);
  }

  isAuthenticated() {
    return !!this.token;
  }

  // safe parser: returns username or null; wraps in try/catch
  private getUsernameFromToken(): string | null {
    try {
      if (typeof window === 'undefined' || typeof localStorage === 'undefined') return null;
      const t = localStorage.getItem(this.KEY);
      if (!t) return null;
      const parts = t.split('.');
      if (parts.length < 2) return null;
      const payload = JSON.parse(atob(parts[1]));
      // JWT spec uses "sub" as subject; some implementations use "username"
      return payload.sub || payload.username || null;
    } catch (e) {
      return null;
    }
  }
  
}
