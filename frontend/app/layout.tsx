import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Palmery Auth",
  description: "Auth frontend for register/login and token flow",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
