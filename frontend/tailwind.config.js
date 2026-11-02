/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        gold: {
          50: '#FDFBF7',
          100: '#FAF4E6',
          200: '#F4E7C4',
          300: '#EBD699',
          400: '#DEC06B',
          500: '#D4AF37', // Heritage Gold
          600: '#B8860B', // Dark Goldenrod
          700: '#946B08',
          800: '#75540B',
          900: '#5F440E',
        },
        navy: {
          800: '#0F172A',
          900: '#0A0F1D',
          950: '#05070E',
        }
      },
    },
  },
  plugins: [],
}

