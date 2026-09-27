import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    port: 5569,
    proxy: {
      '/api': { target: 'http://127.0.0.1:5568', changeOrigin: true },
      '/admin': { target: 'http://127.0.0.1:5568', changeOrigin: true },
      '/houses': { target: 'http://127.0.0.1:5568', changeOrigin: true },
      '/banners': { target: 'http://127.0.0.1:5568', changeOrigin: true },
    },
  },
})
