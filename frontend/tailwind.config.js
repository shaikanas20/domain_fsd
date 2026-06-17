/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class', // Enable class-based dark mode
  theme: {
    extend: {
      colors: {
        brand: {
          red: '#E50914',
          dark: '#141414',
          gray: '#181818',
          light: '#2F2F2F',
        }
      }
    },
  },
  plugins: [],
}
