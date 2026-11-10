import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";

// Both UI references (docs/ui-reference/) use an Inter-style sans-serif.
const inter = Inter({
  variable: "--font-inter",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Personal Information Manager",
  description: "View and search the messages you send to your Telegram bot.",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html lang="en" className={`${inter.variable} h-full antialiased`}>
      <body className="min-h-full flex flex-col">{children}</body>
    </html>
  );
}
