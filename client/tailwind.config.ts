import type { Config } from 'tailwindcss'

export default {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        display: ['Noto Sans Display', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'Oxygen',
          'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
          'sans-serif'],
        sans: ['Noto Sans Display', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'Oxygen',
          'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
          'sans-serif'],
        serif: ['Noto Serif Display', 'Georgia', 'Cambria', 'Times New Roman', 'Times', 'serif'],
        heading: ['Montserrat'],
        mono: ['Fragment Mono', 'Menlo', 'Monaco', 'Consolas', 'Courier New',
          'monospace'],
        symbols: ["Material Symbols Rounded"]
      },
      colors: {
        // GNOME 40 like colors
        container: "#404040",
        background: "#282828",
        primary: "#F9F9F9",
        onPrimary: "#090909",
        secondary: "#C0C0C0",
        error: "rgb(239 68 68)"
      }
    },
  },
  plugins: [
    require("@kobalte/tailwindcss"),
  ],
} satisfies Config

