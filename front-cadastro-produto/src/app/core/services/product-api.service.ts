import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { ApiResponse, Product, ProductInput, ProductPage, StockMovementInput } from '../models/product.model';
import { ApiConfigService } from './api-config.service';

export class ApiError extends Error {
  constructor(message: string, readonly status: number, readonly details?: unknown) {
    super(message);
  }
}

@Injectable({ providedIn: 'root' })
export class ProductApiService {
  private readonly http = inject(HttpClient);
  private readonly config = inject(ApiConfigService);

  list(page = 1, size = 10): Observable<ProductPage> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.request(this.http.get<ProductPage>(`${this.config.baseUrl}/products`, { params }));
  }

  findById(id: number): Observable<Product> {
    return this.request(this.http.get<ApiResponse<Product>>(`${this.config.baseUrl}/products/${id}`)).pipe(map((response) => response.data));
  }

  create(product: ProductInput, image: File | null): Observable<Product> {
    return this.request(this.http.post<ApiResponse<Product>>(`${this.config.baseUrl}/products`, this.multipart(product, image))).pipe(map((response) => response.data));
  }

  update(id: number, product: ProductInput, image: File | null): Observable<Product> {
    return this.request(this.http.put<ApiResponse<Product>>(`${this.config.baseUrl}/products/${id}`, this.multipart(product, image))).pipe(map((response) => response.data));
  }

  delete(id: number): Observable<void> {
    return this.request(this.http.delete<void>(`${this.config.baseUrl}/products/${id}`));
  }

  moveStock(id: number, movement: StockMovementInput): Observable<void> {
    return this.request(this.http.post<void>(`${this.config.baseUrl}/products/${id}/stock-movements`, movement));
  }

  resolveImageUrl(path: string | null): string | null {
    if (!path) return null;
    try {
      const apiUrl = new URL(this.config.baseUrl);
      const resolved = new URL(path, `${apiUrl.origin}/`);
      return ['http:', 'https:'].includes(resolved.protocol) ? resolved.href : null;
    } catch {
      return null;
    }
  }

  private multipart(product: ProductInput, image: File | null): FormData {
    const body = new FormData();
    body.append('product', new Blob([JSON.stringify(product)], { type: 'application/json' }));
    if (image) body.append('image', image, image.name);
    return body;
  }

  private request<T>(request: Observable<T>): Observable<T> {
    return request.pipe(
      catchError((error: HttpErrorResponse) => {
        const message = error.status === 0
          ? 'Não foi possível conectar à API. Confirme se o back-end está em execução.'
          : error.error?.message ?? error.error?.detail ?? `A API retornou o status ${error.status}.`;
        return throwError(() => new ApiError(message, error.status, error.error));
      })
    );
  }
}
