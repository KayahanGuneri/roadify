/**
 * AuthContext.tsx
 *
 * English:
 * Minimal auth context to store access token.
 *
 * Türkçe Özet:
 * Access token'ı app boyunca taşır. Trip endpointleri için gerekir.
 */

import React, { createContext, useContext, useMemo, useState } from 'react';
import { loginWithPassword } from '../api/auth';


// 1) JWT decode için import
import { jwtDecode } from 'jwt-decode';

// 2) Payload tipi (iss'i görmek için yeterli)
type AnyJwtPayload = {
    iss?: string;
    sub?: string;
    preferred_username?: string;
    exp?: number;
    iat?: number;
    [key: string]: any;
};

type AuthContextValue = {
    accessToken: string | null;
    login: (username: string, password: string) => Promise<void>;
    logout: () => void;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [accessToken, setAccessToken] = useState<string | null>(null);

    const login = async (username: string, password: string) => {
        // loginWithPassword'in döndürdüğü response bir obje (access_token alanı var)
        const token = await loginWithPassword(username, password);

        const rawToken = token.access_token;

        // 3) Token'ın ilk kısmını logla
        console.log('[AUTH] access token (first 40 chars):', rawToken.substring(0, 40));

        // 4) JWT payload'ını decode edip iss'i logla
        try {
            const payload = jwtDecode<AnyJwtPayload>(rawToken);
            console.log('[AUTH] token payload:', payload);
            console.log('[AUTH] token iss:', payload.iss);
        } catch (e) {
            console.log('[AUTH] Failed to decode JWT:', e);
        }

        // 5) Context'e raw access token'ı koy
        setAccessToken(rawToken);
    };

    const logout = () => setAccessToken(null);

    const value = useMemo(() => ({ accessToken, login, logout }), [accessToken]);

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export function useAuth(): AuthContextValue {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error('useAuth must be used within AuthProvider');
    return ctx;
}
