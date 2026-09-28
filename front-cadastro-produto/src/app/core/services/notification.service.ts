import { Injectable, signal } from '@angular/core';

export interface Notification {
  message: string;
  type: 'success' | 'error';
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  readonly current = signal<Notification | null>(null);
  private timer: number | undefined;

  show(message: string, type: Notification['type']): void {
    this.current.set({ message, type });
    window.clearTimeout(this.timer);
    this.timer = window.setTimeout(() => this.current.set(null), 4000);
  }

  close(): void {
    window.clearTimeout(this.timer);
    this.current.set(null);
  }
}
