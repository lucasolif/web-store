export interface Product {
  id: number;
  name: string;
  description: string | null;
  price: number;
  stockQuantity: number;
  imagePath: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ProductInput {
  name: string;
  description: string | null;
  price: number;
}

export interface Pagination {
  currentPage: number;
  totalPages: number;
  pageSize: number;
  totalRecords: number;
  recordsOnPage: number;
  hasNextPage: boolean;
  hasPreviousPage: boolean;
}

export interface ProductPage {
  data: Product[];
  pagination: Pagination;
}

export interface ApiResponse<T> {
  data: T;
}

export type StockMovementType = 'INBOUND' | 'OUTBOUND';

export interface StockMovementInput {
  quantity: number;
  operation: StockMovementType;
}
