import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { ProductListComponent } from './components/product-list/product-list';
import { CartComponent } from './components/cart/cart.component';
import { CheckoutComponent } from './components/checkout/checkout.component';
import { OrderSuccessComponent } from './components/order-success/order-success.component';
import { TokenInterceptor } from './interceptors/token.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    // Provide HttpClientModule + FormsModule for the app
    importProvidersFrom(HttpClientModule, FormsModule),

    // Provide the router with standalone component routes
    provideRouter([
      { path: '', component: ProductListComponent },
      { path: 'cart', component: CartComponent },
      { path: 'checkout', component: CheckoutComponent },
      { path: 'order-success/:id', component: OrderSuccessComponent },
      // add login/register routes if you created them
      // { path: 'login', component: LoginComponent },
      // { path: 'register', component: RegisterComponent },
      { path: '**', redirectTo: '' }
    ]),

    // Provide HttpClient with interceptors from DI
    provideHttpClient(withInterceptorsFromDi()),

    // Register the token interceptor
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true }
  ]
};
