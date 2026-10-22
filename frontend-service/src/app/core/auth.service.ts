import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginResponse } from '../models/login-response';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly loginUrl = 'http://localhost:8080/api/auth/login';

  readonly token = signal<string | null>(null);
  readonly username = signal<string | null>(null);

  readonly isAuthenticated = computed(() => this.token() !== null);

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(this.loginUrl, { username, password })
      .pipe(
        tap((response) => {
          this.token.set(response.accessToken);
          this.username.set(username);
        }),
      );
  }

  logout(): void {
    this.token.set(null);
    this.username.set(null);
  }
}
