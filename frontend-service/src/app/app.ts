import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

interface AuthenticatedUser {
  username: string;
}

@Component({
  imports: [FormsModule],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly username = signal('');
  protected readonly password = signal('');
  protected readonly loggedInUsername = signal<string | null>(null);
  protected readonly errorMessage = signal('');
  protected readonly loading = signal(false);

  login(): void {
    this.errorMessage.set('');
    this.loading.set(true);

    const token = btoa(`${this.username()}:${this.password()}`);

    fetch('http://localhost:8080/api/auth/me', {
      headers: { Authorization: `Basic ${token}` },
    })
      .then(async (response) => {
        if (!response.ok) {
          throw new Error(
            response.status === 401
              ? 'Incorrect login or password'
              : `API error: ${response.status}`,
          );
        }

        return (await response.json()) as AuthenticatedUser;
      })
      .then((user) => this.loggedInUsername.set(user.username))
      .catch((error: Error) => this.errorMessage.set(error.message))
      .finally(() => this.loading.set(false));
  }
}
