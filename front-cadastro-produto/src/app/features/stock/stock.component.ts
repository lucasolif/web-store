import { DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin, finalize } from 'rxjs';
import { Product, StockMovementInput, StockMovementType } from '../../core/models/product.model';
import { NotificationService } from '../../core/services/notification.service';
import { ProductApiService } from '../../core/services/product-api.service';

@Component({
  selector: 'app-stock',
  imports: [ReactiveFormsModule, DecimalPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section class="stock-hero mb-4">
      <div><p class="eyebrow mb-2"><span></span>Controle operacional</p><h1>Movimentação de estoque</h1><p>Registre entradas e saídas mantendo a quantidade disponível sempre atualizada.</p></div>
      <span class="stock-hero-icon"><i class="bi bi-boxes"></i></span>
    </section>

    <div class="stock-layout">
      <section class="stock-form-card">
        <div class="stock-card-heading"><span class="section-icon"><i class="bi bi-arrow-left-right"></i></span><div><h2>Nova movimentação</h2><p>Selecione o produto, a operação e informe a quantidade.</p></div></div>

        @if (loading()) { <div class="loading-state stock-loading"><div class="spinner-border text-primary"></div><span>Carregando produtos...</span></div> }
        @else if (loadError()) { <div class="alert alert-danger">{{ loadError() }}</div><button class="btn btn-outline-primary" (click)="loadProducts()">Tentar novamente</button> }
        @else {
          <form [formGroup]="form" (ngSubmit)="submit()" class="stock-form">
            <label class="form-label" for="product">Produto *</label>
            <select id="product" class="form-select" formControlName="productId" (change)="clearFeedback()">
              <option [ngValue]="0" disabled>Selecione um produto</option>
              @for (product of products(); track product.id) { <option [ngValue]="product.id">#{{ product.id }} — {{ product.name }}</option> }
            </select>

            @if (selectedProduct(); as product) {
              <div class="selected-product-card">
                <div class="selected-product-image">@if (api.resolveImageUrl(product.imagePath); as url) { <img [src]="url" [alt]="product.name"> } @else { <i class="bi bi-box-seam"></i> }</div>
                <div><small>Produto selecionado</small><strong>{{ product.name }}</strong><span>{{ product.description || 'Sem descrição' }}</span></div>
                <div class="current-stock"><small>Estoque atual</small><strong>{{ product.stockQuantity | number:'1.0-2' }}</strong></div>
              </div>
            }

            <fieldset class="movement-fieldset"><legend>Operação *</legend><div class="movement-options">
              <label class="movement-option inbound" [class.selected]="form.controls.operation.value === 'INBOUND'"><input type="radio" formControlName="operation" value="INBOUND" (change)="clearFeedback()"><span class="movement-icon"><i class="bi bi-arrow-down-left"></i></span><span><strong>Entrada</strong><small>Adicionar ao estoque</small></span><i class="selection-dot"></i></label>
              <label class="movement-option outbound" [class.selected]="form.controls.operation.value === 'OUTBOUND'"><input type="radio" formControlName="operation" value="OUTBOUND" (change)="clearFeedback()"><span class="movement-icon"><i class="bi bi-arrow-up-right"></i></span><span><strong>Saída</strong><small>Retirar do estoque</small></span><i class="selection-dot"></i></label>
            </div></fieldset>

            <label class="form-label" for="quantity">Quantidade *</label>
            <div class="input-group quantity-input"><span class="input-group-text"><i class="bi bi-123"></i></span><input id="quantity" class="form-control" type="number" min="0.01" step="0.01" inputmode="decimal" formControlName="quantity" (input)="clearFeedback()"><span class="input-group-text">unidade(s)</span></div>
            @if (form.controls.quantity.invalid && form.controls.quantity.touched) { <div class="validation-message">Informe uma quantidade maior que zero.</div> }

            @if (selectedProduct(); as product) {
              <div class="stock-projection" [class.projection-danger]="projectedStock() < 0"><span><small>Saldo projetado</small><strong>{{ product.stockQuantity | number:'1.0-2' }} {{ form.controls.operation.value === 'INBOUND' ? '+' : '−' }} {{ form.controls.quantity.value | number:'1.0-2' }}</strong></span><i class="bi bi-arrow-right"></i><strong>{{ projectedStock() | number:'1.0-2' }}</strong></div>
            }

            @if (errorMessage()) { <div class="alert alert-danger mb-0">{{ errorMessage() }}</div> }
            @if (successMessage()) { <div class="alert alert-success mb-0"><i class="bi bi-check-circle-fill me-2"></i>{{ successMessage() }}</div> }

            <button class="btn btn-primary btn-lg stock-submit" type="submit" [disabled]="submitting() || form.invalid || projectedStock() < 0">@if (submitting()) { <span class="spinner-border spinner-border-sm"></span> }<i class="bi bi-check2-circle"></i>Confirmar movimentação</button>
          </form>
        }
      </section>

      <aside class="stock-guidance">
        <div class="guidance-icon"><i class="bi bi-info-circle"></i></div><h2>Como funciona</h2>
        <div class="guidance-item"><span>1</span><div><strong>Escolha o produto</strong><small>O estoque atual será exibido para conferência.</small></div></div>
        <div class="guidance-item"><span>2</span><div><strong>Defina a operação</strong><small>Entrada adiciona e saída reduz a quantidade.</small></div></div>
        <div class="guidance-item"><span>3</span><div><strong>Confirme a quantidade</strong><small>O saldo será atualizado imediatamente pela API.</small></div></div>
        <div class="guidance-warning"><i class="bi bi-shield-exclamation"></i><span>Uma saída maior que o saldo disponível será bloqueada.</span></div>
      </aside>
    </div>
  `
})
export class StockComponent {
  readonly api = inject(ProductApiService);
  private readonly notifications = inject(NotificationService);
  private readonly fb = inject(NonNullableFormBuilder);
  readonly products = signal<Product[]>([]);
  readonly loading = signal(true);
  readonly loadError = signal('');
  readonly submitting = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');
  private readonly formRevision = signal(0);

  readonly form = this.fb.group({
    productId: [0, [Validators.required, Validators.min(1)]],
    operation: this.fb.control<StockMovementType>('INBOUND', Validators.required),
    quantity: [0, [Validators.required, Validators.min(0.01)]]
  });

  readonly selectedProduct = computed(() => {
    this.formRevision();
    return this.products().find((product) => product.id === this.form.controls.productId.value) ?? null;
  });
  readonly projectedStock = computed(() => {
    this.formRevision();
    const product = this.selectedProduct();
    if (!product) return 0;
    const quantity = Number(this.form.controls.quantity.value) || 0;
    return this.form.controls.operation.value === 'INBOUND' ? Number(product.stockQuantity) + quantity : Number(product.stockQuantity) - quantity;
  });

  constructor() {
    this.loadProducts();
    this.form.valueChanges.subscribe(() => this.formRevision.update((value) => value + 1));
  }

  loadProducts(): void {
    this.loading.set(true); this.loadError.set('');
    this.api.list(1, 100).subscribe({
      next: (first) => {
        if (first.pagination.totalPages <= 1) { this.finishLoading(first.data); return; }
        forkJoin(Array.from({ length: first.pagination.totalPages - 1 }, (_, index) => this.api.list(index + 2, 100)))
          .subscribe({ next: (remaining) => this.finishLoading([...(first.data ?? []), ...remaining.flatMap((page) => page.data ?? [])]), error: (error: Error) => this.failLoading(error) });
      },
      error: (error: Error) => this.failLoading(error)
    });
  }

  submit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.projectedStock() < 0) {
      if (this.projectedStock() < 0) this.errorMessage.set('A saída informada é maior que o estoque disponível.');
      return;
    }
    const value = this.form.getRawValue();
    const movement: StockMovementInput = { operation: value.operation, quantity: Number(value.quantity) };
    this.submitting.set(true); this.errorMessage.set(''); this.successMessage.set('');
    this.api.moveStock(value.productId, movement).pipe(finalize(() => this.submitting.set(false))).subscribe({
      next: () => this.refreshProduct(value.productId, value.operation),
      error: (error: Error) => this.errorMessage.set(error.message)
    });
  }

  clearFeedback(): void { this.errorMessage.set(''); this.successMessage.set(''); }

  private refreshProduct(id: number, operation: StockMovementType): void {
    this.api.findById(id).subscribe({
      next: (updated) => {
        this.products.update((products) => products.map((product) => product.id === id ? updated : product));
        const message = `${operation === 'INBOUND' ? 'Entrada' : 'Saída'} registrada. Novo estoque: ${updated.stockQuantity}.`;
        this.successMessage.set(message); this.notifications.show('Estoque atualizado com sucesso.', 'success'); this.form.controls.quantity.setValue(0); this.form.controls.quantity.markAsUntouched();
      },
      error: (error: Error) => this.errorMessage.set(`Movimentação concluída, mas não foi possível atualizar a consulta: ${error.message}`)
    });
  }

  private finishLoading(products: Product[]): void { this.products.set([...products].sort((a, b) => a.name.localeCompare(b.name, 'pt-BR'))); this.loading.set(false); }
  private failLoading(error: Error): void { this.loadError.set(error.message); this.loading.set(false); }
}
