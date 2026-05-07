// tailwind.config.js: Tailwind CSS configuration with brand color #1B4F8A and Inter font.
// Extends the default theme with the project's design system tokens.

/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          50:  "#e8eef6",
          100: "#c5d4e8",
          200: "#9eb8d9",
          300: "#779bc9",
          400: "#5986be",
          500: "#1B4F8A",   // Primary brand color
          600: "#17437a",
          700: "#123666",
          800: "#0d2a52",
          900: "#081e3e",
        },
        slate: {
          850: "#1a2332",
        }
      },
      fontFamily: {
        sans: ["Inter", "Arial", "sans-serif"],
      },
      animation: {
        "fade-in":    "fadeIn 0.2s ease-out",
        "slide-in":   "slideIn 0.3s ease-out",
        "pulse-slow": "pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite",
      },
      keyframes: {
        fadeIn: {
          "0%":   { opacity: "0" },
          "100%": { opacity: "1" },
        },
        slideIn: {
          "0%":   { opacity: "0", transform: "translateY(-8px)" },
          "100%": { opacity: "1", transform: "translateY(0)" },
        },
      },
      boxShadow: {
        "card":  "0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1)",
        "card-hover": "0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1)",
      },
    },
  },
  plugins: [],
}