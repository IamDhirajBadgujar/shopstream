import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { ProductListComponent } from './components/product-list/product-list';
import { CartComponent } from './components/cart/cart.component';
import { CheckoutComponent } from './components/checkout/checkout.component';
import { OrderSuccessComponent } from './components/order-success/order-success.component';

export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(HttpClientModule, FormsModule),
    provideRouter([
      { path: '', component: ProductListComponent },
      { path: 'cart', component: CartComponent },
      { path: 'checkout', component: CheckoutComponent },
      { path: 'order-success/:id', component: OrderSuccessComponent },
      { path: '**', redirectTo: '' }
    ])
  ]
};
