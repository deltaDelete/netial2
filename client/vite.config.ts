import { defineConfig } from 'vite';
import solidPlugin from 'vite-plugin-solid';
import { dirname, resolve } from "node:path";
// import devtools from 'solid-devtools/vite';

import { fileURLToPath } from "node:url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

export default defineConfig({
  plugins: [
    // devtools(),
    solidPlugin(),
  ],
  server: {
    port: 3000,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, ""),
      },
    }
  },
  build: {
    target: 'esnext',
  },
  resolve: {
    alias: {
      "@": resolve(__dirname, "./src"),
      "@components": resolve(__dirname, "./src/components"),
      "@routes": resolve(__dirname, "./src/routes"),
      "@assets": resolve(__dirname, "./src/assets"),
    }
  },
});
