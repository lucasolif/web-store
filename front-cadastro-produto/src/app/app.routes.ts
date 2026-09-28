import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'produtos',
    title: 'Produtos | WebStore',
    loadComponent: () => import('./features/products/products.component').then((module) => module.ProductsComponent)
  },
  {
    path: 'estoque',
    title: 'Movimentar estoque | WebStore',
    loadComponent: () => import('./features/stock/stock.component').then((module) => module.StockComponent)
  },
  { path: '', pathMatch: 'full', redirectTo: 'produtos' },
  { path: '**', redirectTo: 'produtos' }
];
