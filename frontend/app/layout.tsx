import type { Metadata } from "next";
import "./globals.css";
import Footer from "@/components/ui/footer";
import { AuthProvider } from "@/context/AuthContext";

export const metadata: Metadata = {
  title: "Palmery Auth",
  description: "Auth frontend",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body>

        <AuthProvider>
          {children}
        </AuthProvider>

        <Footer />

      </body>
    </html>
  );
}
