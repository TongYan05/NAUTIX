import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'node:path'

export default defineConfig({
  plugins: [
    vue()
  ],

  define: {
    CESIUM_BASE_URL:
        JSON.stringify('/cesium')
  },

  resolve: {
    alias: {
      '@':
          path.resolve(
              import.meta.dirname,
              'src'
          )
    }
  },

  publicDir: 'public'
})
