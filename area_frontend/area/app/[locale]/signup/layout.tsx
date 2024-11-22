import type { Metadata } from "next";
import { NextIntlClientProvider } from 'next-intl';
import { getMessages } from 'next-intl/server';
import localFont from "next/font/local";
import "../../globals.css";
import '@fontsource/roboto/300.css';
import '@fontsource/roboto/400.css';
import '@fontsource/roboto/500.css';
import '@fontsource/roboto/700.css';
import { GoogleOAuthProvider } from '@react-oauth/google';

const geistSans = localFont({
    src: "../../fonts/GeistVF.woff",
    variable: "--font-geist-sans",
    weight: "100 900",
});
const geistMono = localFont({
    src: "../../fonts/GeistMonoVF.woff",
    variable: "--font-geist-mono",
    weight: "100 900",
});

export const metadata: Metadata = {
    title: "Login Page",
    description: "Generated by create next app",
};

export default async function LocaleLayout({
    children,
    params: { locale }
}: {
    children: React.ReactNode;
    params: { locale: string };
}) {
    const messages = await getMessages();
    const clientidString = process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID || "h";

    return (
        <GoogleOAuthProvider clientId={clientidString}>
            <NextIntlClientProvider messages={messages}>
                <div className={`${geistSans.variable} ${geistMono.variable} antialiased`}>
                    {children}
                </div>
            </NextIntlClientProvider>
        </GoogleOAuthProvider>
    );
}
