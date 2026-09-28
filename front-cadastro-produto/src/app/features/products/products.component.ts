import { CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormControl, NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { forkJoin, finalize } from 'rxjs';
import { Pagination, Product, ProductInput } from '../../core/models/product.model';
import { ApiError, ProductApiService } from '../../core/services/product-api.service';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'app-products',
  imports: [CurrencyPipe, DatePipe, DecimalPipe, ReactiveFormsModule, FormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section class="catalog-hero mb-4" aria-labelledby="pageTitle">
      <div class="hero-grid"></div><div class="hero-content"><div class="hero-heading">
        <p class="eyebrow mb-2"><span></span>Gestão de catálogo</p><h1 id="pageTitle" class="mb-2">Produtos</h1>
        <p class="hero-description mb-0">Organize preços, imagens e disponibilidade dos itens da sua loja em um só lugar.</p>
      </div><button class="btn btn-primary btn-lg hero-action" type="button" (click)="openCreate()"><i class="bi bi-plus-lg"></i>Novo produto</button></div>
      <div class="catalog-metrics">
        <div class="metric-card"><span class="metric-icon metric-icon-primary"><i class="bi bi-boxes"></i></span><span class="metric-copy"><strong>{{ pagination()?.totalRecords ?? products().length }}</strong><small>itens no catálogo</small></span></div>
        <div class="metric-card"><span class="metric-icon metric-icon-cyan"><i class="bi bi-eye"></i></span><span class="metric-copy"><strong>{{ products().length }}</strong><small>exibidos agora</small></span></div>
        <div class="metric-card"><span class="metric-icon metric-icon-violet"><i class="bi bi-layers"></i></span><span class="metric-copy"><strong>{{ searchMode() ? 'Busca' : currentPage() }}</strong><small>página atual</small></span></div>
      </div>
    </section>

    <section class="toolbar-card mb-4"><form class="row g-3 align-items-center" (ngSubmit)="search()">
      <div class="col-12 col-lg"><label class="toolbar-label" for="search">Localizar produto</label><div class="input-group search-group"><span class="input-group-text"><i class="bi bi-search"></i></span><input id="search" class="form-control" type="search" [(ngModel)]="searchTerm" name="searchTerm" placeholder="Código, nome ou descrição"></div></div>
      <div class="col-12 col-sm-auto d-flex gap-2 toolbar-actions"><button class="btn btn-dark flex-grow-1" type="submit">Buscar</button>@if (searchMode()) { <button class="btn btn-outline-secondary" type="button" (click)="clearSearch()">Limpar</button> }<button class="btn btn-outline-secondary" type="button" aria-label="Atualizar" (click)="loadProducts(currentPage())"><i class="bi bi-arrow-clockwise"></i></button></div>
    </form></section>

    <section class="content-card" aria-labelledby="listTitle">
      <div class="content-card-header d-flex flex-column flex-sm-row align-items-sm-center justify-content-between gap-2"><div class="d-flex align-items-center gap-3"><span class="section-icon"><i class="bi bi-grid-1x2-fill"></i></span><div><h2 id="listTitle" class="h5 mb-1">Produtos cadastrados</h2><p class="list-summary mb-0">{{ summary() }}</p></div></div>@if (!loading()) { <span class="result-count">{{ resultCount() }} {{ resultCount() === 1 ? 'produto' : 'produtos' }}</span> }</div>

      @if (loading()) { <div class="loading-state"><div class="spinner-border text-primary"></div><span>Consultando a API...</span></div> }
      @else if (errorMessage()) { <div class="state-panel"><span class="state-icon state-icon-error"><i class="bi bi-wifi-off"></i></span><h3 class="h5">Não foi possível carregar os produtos</h3><p class="text-secondary">{{ errorMessage() }}</p><button class="btn btn-outline-primary" (click)="loadProducts(currentPage())">Tentar novamente</button></div> }
      @else if (products().length === 0) { <div class="state-panel"><span class="state-icon"><i class="bi bi-box"></i></span><h3 class="h5">{{ searchMode() ? 'Nenhum produto encontrado' : 'Nenhum produto cadastrado' }}</h3><p class="text-secondary">{{ searchMode() ? 'Revise o termo ou limpe a busca.' : 'Cadastre o primeiro produto para começar.' }}</p><button class="btn btn-primary" (click)="searchMode() ? clearSearch() : openCreate()">{{ searchMode() ? 'Limpar busca' : 'Cadastrar produto' }}</button></div> }
      @else { <div class="table-responsive"><table class="table product-table align-middle mb-0"><thead><tr><th>Produto</th><th>Código</th><th>Preço</th><th>Estoque</th><th>Atualização</th><th class="text-end">Ações</th></tr></thead><tbody>
        @for (product of products(); track product.id) { <tr>
          <td data-label="Produto"><div class="product-cell">@if (imageUrl(product); as url) { <img class="product-thumbnail" [src]="url" [alt]="'Imagem de ' + product.name" (error)="breakImage(product.id)"> } @else { <span class="product-thumbnail-placeholder"><i class="bi bi-box-seam"></i></span> }<div><div class="product-name">{{ product.name }}</div><div class="product-description">{{ product.description || 'Sem descrição' }}</div></div></div></td>
          <td data-label="Código"><span class="code-value">{{ product.id }}</span></td><td data-label="Preço"><strong>{{ product.price | currency:'BRL' }}</strong></td>
          <td data-label="Estoque"><span class="stock-badge" [class.empty]="product.stockQuantity === 0">{{ product.stockQuantity | number:'1.0-2' }}</span></td>
          <td data-label="Atualização"><span class="small text-secondary">{{ product.updatedAt | date:'short' }}</span></td>
          <td data-label="Ações" class="text-end"><div class="action-buttons"><button class="btn btn-outline-primary" (click)="openEdit(product.id)" aria-label="Editar"><i class="bi bi-pencil"></i></button><button class="btn btn-outline-danger" (click)="openDelete(product)" aria-label="Excluir"><i class="bi bi-trash3"></i></button></div></td>
        </tr> }
      </tbody></table></div>
      @if (!searchMode() && (pagination()?.totalPages ?? 1) > 1) { <nav class="pagination-bar d-flex"><button class="btn btn-sm btn-outline-secondary" [disabled]="!pagination()?.hasPreviousPage" (click)="loadProducts(currentPage() - 1)"><i class="bi bi-chevron-left me-1"></i>Anterior</button><span class="pagination-text">Página {{ currentPage() }} de {{ pagination()?.totalPages }}</span><button class="btn btn-sm btn-outline-secondary" [disabled]="!pagination()?.hasNextPage" (click)="loadProducts(currentPage() + 1)">Próxima<i class="bi bi-chevron-right ms-1"></i></button></nav> }
      }
    </section>

    @if (productDialogOpen()) { <div class="modal-backdrop-custom" (click)="closeProductDialog()"><section class="dialog-card dialog-large" role="dialog" aria-modal="true" (click)="$event.stopPropagation()">
      <div class="dialog-header"><div class="d-flex align-items-center gap-3"><span class="modal-heading-icon"><i class="bi bi-box-seam"></i></span><div><p class="eyebrow mb-1">{{ editingId() ? 'Produto #' + editingId() : 'Novo cadastro' }}</p><h2>{{ editingId() ? 'Alterar produto' : 'Cadastrar produto' }}</h2></div></div><button class="dialog-close" (click)="closeProductDialog()"><i class="bi bi-x-lg"></i></button></div>
      <form [formGroup]="productForm" (ngSubmit)="saveProduct()"><div class="dialog-body"><div class="row g-4"><div class="col-12 col-md-7">
        <label class="form-label">Nome *</label><input class="form-control mb-3" formControlName="name" maxlength="150" [class.is-invalid]="invalid(productForm.controls.name)">
        <label class="form-label">Descrição</label><textarea class="form-control" formControlName="description" rows="4" maxlength="500"></textarea><div class="form-text text-end mb-3">{{ productForm.controls.description.value.length }}/500</div>
        <label class="form-label">Preço *</label><div class="input-group"><span class="input-group-text">R$</span><input class="form-control" type="number" min="0.01" step="0.01" formControlName="price" [class.is-invalid]="invalid(productForm.controls.price)"></div>
      </div><div class="col-12 col-md-5 image-column"><label class="form-label">Imagem</label><div class="image-preview mb-3">@if (previewUrl()) { <img [src]="previewUrl()" alt="Prévia da imagem" (error)="previewUrl.set(null)"> } @else { <div class="image-placeholder"><i class="bi bi-image"></i><span>Nenhuma imagem</span></div> }</div><input class="form-control" type="file" accept="image/jpeg,image/png,image/webp" (change)="selectImage($event)"><div class="form-text">JPEG, PNG ou WebP, até 5 MB. Ao editar, deixe vazio para manter a imagem atual.</div></div></div>
        @if (formError()) { <div class="alert alert-danger mt-3 mb-0">{{ formError() }}</div> }
      </div><div class="dialog-actions"><button class="btn btn-outline-secondary" type="button" (click)="closeProductDialog()">Cancelar</button><button class="btn btn-primary" type="submit" [disabled]="saving()">@if (saving()) { <span class="spinner-border spinner-border-sm me-2"></span> }{{ editingId() ? 'Salvar alterações' : 'Cadastrar produto' }}</button></div></form>
    </section></div> }

    @if (deletingProduct(); as product) { <div class="modal-backdrop-custom" (click)="closeDelete()"><section class="dialog-card dialog-small" role="alertdialog" aria-modal="true" (click)="$event.stopPropagation()"><div class="dialog-header"><h2>Excluir produto</h2><button class="dialog-close" (click)="closeDelete()"><i class="bi bi-x-lg"></i></button></div><div class="dialog-body"><div class="danger-prompt"><i class="bi bi-exclamation-triangle"></i></div><p>Deseja realmente excluir <strong>{{ product.name }}</strong>?</p><p class="text-secondary mb-0">A operação não poderá ser desfeita.</p>@if (deleteError()) { <div class="alert alert-danger mt-3 mb-0">{{ deleteError() }}</div> }</div><div class="dialog-actions"><button class="btn btn-outline-secondary" (click)="closeDelete()">Cancelar</button><button class="btn btn-danger" [disabled]="deleting()" (click)="confirmDelete()">Excluir produto</button></div></section></div> }
  `
})
export class ProductsComponent {
  private readonly api = inject(ProductApiService);
  private readonly notifications = inject(NotificationService);
  private readonly fb = inject(NonNullableFormBuilder);
  readonly products = signal<Product[]>([]);
  readonly pagination = signal<Pagination | null>(null);
  readonly currentPage = signal(1);
  readonly loading = signal(true);
  readonly errorMessage = signal('');
  readonly searchMode = signal(false);
  readonly productDialogOpen = signal(false);
  readonly editingId = signal<number | null>(null);
  readonly previewUrl = signal<string | null>(null);
  readonly selectedImage = signal<File | null>(null);
  readonly saving = signal(false);
  readonly formError = signal('');
  readonly deletingProduct = signal<Product | null>(null);
  readonly deleting = signal(false);
  readonly deleteError = signal('');
  readonly brokenImages = signal(new Set<number>());
  searchTerm = '';

  readonly productForm = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(150)]],
    description: ['', Validators.maxLength(500)],
    price: [0, [Validators.required, Validators.min(0.01)]]
  });

  constructor() { this.loadProducts(1); }

  loadProducts(page: number): void {
    if (page < 1) return;
    this.loading.set(true); this.errorMessage.set(''); this.searchMode.set(false);
    this.api.list(page, 10).pipe(finalize(() => this.loading.set(false))).subscribe({
      next: (response) => { this.products.set(response.data ?? []); this.pagination.set(response.pagination); this.currentPage.set(response.pagination.currentPage); },
      error: (error: Error) => this.errorMessage.set(error.message)
    });
  }

  summary(): string {
    if (this.loading()) return 'Carregando produtos...';
    if (this.errorMessage()) return 'Falha na consulta';
    if (this.searchMode()) return `Resultados para “${this.searchTerm.trim()}”`;
    const total = this.pagination()?.totalRecords ?? this.products().length;
    return total === 1 ? '1 item cadastrado' : `${total} itens cadastrados`;
  }
  resultCount(): number { return this.searchMode() ? this.products().length : (this.pagination()?.totalRecords ?? this.products().length); }

  search(): void {
    const term = this.searchTerm.trim();
    if (!term) { this.clearSearch(); return; }
    this.loading.set(true); this.errorMessage.set(''); this.searchMode.set(true);
    if (/^\d+$/.test(term)) {
      this.api.findById(Number(term)).pipe(finalize(() => this.loading.set(false))).subscribe({ next: (product) => this.products.set([product]), error: (error: ApiError) => error.status === 404 ? this.products.set([]) : this.errorMessage.set(error.message) });
      return;
    }
    this.api.list(1, 100).subscribe({
      next: (first) => {
        const pages = first.pagination.totalPages;
        if (pages <= 1) { this.applyTextSearch(first.data, term); this.loading.set(false); return; }
        forkJoin(Array.from({ length: pages - 1 }, (_, index) => this.api.list(index + 2, 100))).pipe(finalize(() => this.loading.set(false))).subscribe({ next: (remaining) => this.applyTextSearch([...(first.data ?? []), ...remaining.flatMap((page) => page.data ?? [])], term), error: (error: Error) => this.errorMessage.set(error.message) });
      },
      error: (error: Error) => { this.loading.set(false); this.errorMessage.set(error.message); }
    });
  }

  clearSearch(): void { this.searchTerm = ''; this.loadProducts(1); }
  openCreate(): void { this.resetForm(); this.productDialogOpen.set(true); }
  openEdit(id: number): void {
    this.resetForm(); this.saving.set(true);
    this.api.findById(id).pipe(finalize(() => this.saving.set(false))).subscribe({
      next: (product) => { this.editingId.set(id); this.productForm.setValue({ name: product.name, description: product.description ?? '', price: product.price }); this.previewUrl.set(this.api.resolveImageUrl(product.imagePath)); this.productDialogOpen.set(true); },
      error: (error: Error) => this.notifications.show(error.message, 'error')
    });
  }

  closeProductDialog(): void { this.productDialogOpen.set(false); this.releasePreview(); }
  selectImage(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0] ?? null;
    if (!file) return;
    if (!['image/jpeg', 'image/png', 'image/webp'].includes(file.type) || file.size > 5 * 1024 * 1024) { this.formError.set('Selecione uma imagem JPEG, PNG ou WebP de até 5 MB.'); (event.target as HTMLInputElement).value = ''; return; }
    this.releasePreview(); this.selectedImage.set(file); this.previewUrl.set(URL.createObjectURL(file)); this.formError.set('');
  }

  saveProduct(): void {
    this.productForm.markAllAsTouched(); if (this.productForm.invalid) return;
    const value = this.productForm.getRawValue();
    const input: ProductInput = { name: value.name.trim(), description: value.description.trim() || null, price: value.price };
    const id = this.editingId(); const request = id ? this.api.update(id, input, this.selectedImage()) : this.api.create(input, this.selectedImage());
    this.saving.set(true); this.formError.set('');
    request.pipe(finalize(() => this.saving.set(false))).subscribe({
      next: () => { this.closeProductDialog(); this.notifications.show(id ? 'Produto alterado com sucesso.' : 'Produto cadastrado com sucesso.', 'success'); this.loadProducts(id ? this.currentPage() : 1); },
      error: (error: Error) => this.formError.set(error.message)
    });
  }

  openDelete(product: Product): void { this.deletingProduct.set(product); this.deleteError.set(''); }
  closeDelete(): void { this.deletingProduct.set(null); }
  confirmDelete(): void {
    const product = this.deletingProduct(); if (!product) return;
    this.deleting.set(true);
    this.api.delete(product.id).pipe(finalize(() => this.deleting.set(false))).subscribe({
      next: () => { this.closeDelete(); this.notifications.show('Produto excluído com sucesso.', 'success'); this.loadProducts(this.products().length === 1 && this.currentPage() > 1 ? this.currentPage() - 1 : this.currentPage()); },
      error: (error: Error) => this.deleteError.set(error.message)
    });
  }

  invalid(control: FormControl<string | number>): boolean { return control.invalid && (control.touched || control.dirty); }
  imageUrl(product: Product): string | null { return this.brokenImages().has(product.id) ? null : this.api.resolveImageUrl(product.imagePath); }
  breakImage(id: number): void { this.brokenImages.update((items) => new Set(items).add(id)); }

  private applyTextSearch(products: Product[], term: string): void { const normalized = this.normalize(term); this.products.set(products.filter((product) => [product.id, product.name, product.description].some((value) => this.normalize(value).includes(normalized)))); }
  private normalize(value: unknown): string { return String(value ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase(); }
  private resetForm(): void { this.releasePreview(); this.productForm.reset({ name: '', description: '', price: 0 }); this.editingId.set(null); this.selectedImage.set(null); this.formError.set(''); }
  private releasePreview(): void { const url = this.previewUrl(); if (url?.startsWith('blob:')) URL.revokeObjectURL(url); this.previewUrl.set(null); }
}
