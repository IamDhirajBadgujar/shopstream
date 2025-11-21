import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi ,  withFetch} from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ProductList } from './components/product-list/product-list';
import { CartComponent } from './components/cart/cart.component';
import { CheckoutComponent } from './components/checkout/checkout.component';
import { OrderSuccessComponent } from './components/order-success/order-success.component';
import { TokenInterceptor } from './interceptors/token.interceptor';
import { ProfileComponent } from './components/profile/profile.component';
import { authGuard } from './guards/auth.guard';
export const appConfig: ApplicationConfig = {
  providers: [
    // Provide HttpClientModule + FormsModule for the app
    importProvidersFrom(HttpClientModule, FormsModule),

    // Provide the router with standalone component routes
  provideRouter([
  { path: '', component: ProductList },
  { path: 'cart', component: CartComponent, canActivate: [authGuard] },
  { path: 'checkout', component: CheckoutComponent, canActivate: [authGuard] },
  { path: 'order-success/:id', component: OrderSuccessComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
])
,

    // Provide HttpClient with interceptors from DI
  provideHttpClient(withInterceptorsFromDi(), withFetch()),

    // Register the token interceptor
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true }
  ]
};
