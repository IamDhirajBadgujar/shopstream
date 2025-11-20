import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, tap } from 'rxjs';

interface AuthResp { token: string; username: string; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private KEY = 'shopstream_jwt';
  private _user$ = new BehaviorSubject<string | null>(this.getUsernameFromToken());

  user$ = this._user$.asObservable();

  constructor(private http: HttpClient) {}

  register(username: string, email:string, password:string) {
    return this.http.post<AuthResp>('http://localhost:8080/api/auth/register', { username, email, password })
      .pipe(tap(res => this.saveToken(res.token)));
  }

  login(username: string, password: string) {
    return this.http.post<AuthResp>('http://localhost:8080/api/auth/login', { username, password })
      .pipe(tap(res => this.saveToken(res.token)));
  }

  private saveToken(token: string) {
    localStorage.setItem(this.KEY, token);
    this._user$.next(this.getUsernameFromToken());
  }

  logout() {
    localStorage.removeItem(this.KEY);
    this._user$.next(null);
  }

  get token() {
    return localStorage.getItem(this.KEY);
  }

  isAuthenticated() {
    return !!this.token;
  }

  private getUsernameFromToken(): string | null {
    const t = localStorage.getItem(this.KEY);
    if (!t) return null;
    try {
      const payload = JSON.parse(atob(t.split('.')[1]));
      return payload.sub || payload.username || null;
    } catch { return null; }
  }
}
