import { defineConfig } from 'vite'

// 开发时由 Vite 转发 /api，因此浏览器没有跨域问题。
export default defineConfig({
  server: {
    port: 5173,
    proxy: { '/api': 'http://localhost:8081' },
  },
})
