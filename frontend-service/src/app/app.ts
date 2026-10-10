import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

interface Patient {
  firstName: string;
  lastName: string;
  birthDate: string;
  gender: string;
  address: string;
  phone: string;
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
  protected readonly patients = signal<Patient[]>([]);
  protected readonly errorMessage = signal('');
  protected readonly loading = signal(false);

  loadPatients(): void {
    this.errorMessage.set('');
    this.loading.set(true);

    const token = btoa(`${this.username()}:${this.password()}`);

    fetch('http://localhost:8080/api/patients', {
      headers: { Authorization: `Basic ${token}` },
    })
      .then(async (response) => {
        if (!response.ok) {
          throw new Error(
            response.status === 401
              ? 'Incorrect login or password'
              : `Error API: ${response.status}`,
          );
        }

        return (await response.json()) as Patient[];
      })
      .then((patients) => this.patients.set(patients))
      .catch((error: Error) => this.errorMessage.set(error.message))
      .finally(() => this.loading.set(false));
  }
}
