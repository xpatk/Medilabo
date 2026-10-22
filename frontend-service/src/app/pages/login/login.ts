import { Component, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly username = signal('');
  protected readonly password = signal('');
  protected readonly errorMessage = signal('');
  protected readonly loading = signal(false);

  login(): void {
    const username = this.username().trim();
    const password = this.password();

    if (!username || !password) {
      this.errorMessage.set('Enter your username and password.');
      return;
    }

    this.errorMessage.set('');
    this.loading.set(true);

    this.authService.login(username, password).subscribe({
      next: () => {
        this.password.set('');
        void this.router.navigate(['/patients']);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);

        this.errorMessage.set(
          error.status === 401
            ? 'Incorrect username or password.'
            : 'Unable to contact the authentication service.',
        );
      },
    });
  }
}
