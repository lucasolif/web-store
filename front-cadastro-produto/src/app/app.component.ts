import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { NotificationService } from './core/services/notification.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <header class="app-header">
      <nav class="navbar navbar-expand"><div class="container-fluid page-shell">
        <a class="navbar-brand d-flex align-items-center gap-3" routerLink="/produtos">
          <span class="brand-mark"><i class="bi bi-box-seam-fill"></i></span>
          <span class="brand-copy"><strong>WebStore</strong><small>Gestão de produtos</small></span>
        </a>
        <div class="admin-navigation">
          <a routerLink="/produtos" routerLinkActive="active"><i class="bi bi-grid"></i><span>Produtos</span></a>
          <a routerLink="/estoque" routerLinkActive="active"><i class="bi bi-arrow-left-right"></i><span>Estoque</span></a>
        </div>
      </div></nav>
    </header>

    <main class="container-fluid page-shell py-4 py-lg-5"><router-outlet /></main>

    <footer class="app-footer"><div class="container-fluid page-shell d-flex flex-column flex-sm-row justify-content-between gap-2">
      <span>WebStore · Gestão de catálogo</span><span class="footer-meta"><i class="bi bi-shield-check me-1"></i>Integração protegida por validação da API</span>
    </div></footer>

    @if (notifications.current(); as notification) {
      <div class="app-notification" [class.notification-error]="notification.type === 'error'" role="status">
        <i class="bi" [class.bi-check-circle-fill]="notification.type === 'success'" [class.bi-exclamation-circle-fill]="notification.type === 'error'"></i>
        <span>{{ notification.message }}</span><button (click)="notifications.close()"><i class="bi bi-x"></i></button>
      </div>
    }
  `
})
export class AppComponent {
  readonly notifications = inject(NotificationService);
}
