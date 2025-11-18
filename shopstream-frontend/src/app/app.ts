import { Component } from '@angular/core';
import { ProductListComponent } from './components/product-list/product-list';

@Component({
  selector: 'app-root',
  standalone: true,
  // only standalone components / directives / pipes here — NOT HttpClientModule
  imports: [ProductListComponent],
  template: `
    <header style="padding:16px;border-bottom:1px solid #eee">
      <h1 style="margin:0">ShopStream</h1>
    </header>
    <main style="padding:16px">
      <app-product-list></app-product-list>
    </main>
  `,
})
export class App {}
