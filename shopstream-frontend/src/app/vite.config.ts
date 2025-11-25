// vite.config.ts
import { defineConfig } from 'vite';

export default defineConfig({
  server: {
    proxy: {
      '/': {
        target: 'http://localhost:8080',   // your backend
        changeOrigin: true,
        secure: false
      }
    }
  }
});
