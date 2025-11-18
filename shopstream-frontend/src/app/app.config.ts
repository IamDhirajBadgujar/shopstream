import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    // Provide HttpClient globally for the app (correct place)
    importProvidersFrom(HttpClientModule),
  ],
};
