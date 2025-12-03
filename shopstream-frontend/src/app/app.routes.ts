import { Routes } from '@angular/router';

export const routes: Routes = [{ path: 'orders', loadComponent: () => import('./components/order-history/order-history.component').then(m => m.OrderHistoryComponent) }
];
