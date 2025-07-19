import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue' 
import path from 'path'
console.log('API路径:', path.resolve(__dirname, './src/api/auth.js'))

export default defineConfig({
  plugins: [vue()], 
  resolve: {

    alias: {
        '@': path.resolve(__dirname, './src'),
        '@api': path.resolve(__dirname, './src/api') 
    }
  }
})