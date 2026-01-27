/**
 * AuthContext.tsx
 *
 * English:
 * Minimal auth context to store access token and auth state.
 *
 * Türkçe Özet:
 * Access token'ı app boyunca taşır. isLoggedIn ile RootNavigator stack değiştirir.
 */

import React, { createContext, useContext, useMemo, useState } from 'react';
import { loginWithPassword } from '../api/auth';
import { clearAccessToken, setAccessToken as persistAccessToken } from '../lib/tokenStore';

type AuthContextValue = {
    accessToken: string | null;
    isLoggedIn: boolean;
    login: (username: string, password: string) => Promise<void>;
    logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [accessToken, setAccessToken] = useState<string | null>(null);

    const login = async (username: string, password: string) => {
        const token = await loginWithPassword(username, password);
        const rawToken = token.access_token;

        setAccessToken(rawToken);
        await persistAccessToken(rawToken);
    };

    const logout = async () => {
        setAccessToken(null);
        await clearAccessToken();
    };

    const value = useMemo(
        () => ({
            accessToken,
            isLoggedIn: Boolean(accessToken),
            login,
            logout,
        }),
        [accessToken]
    );

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export function useAuth(): AuthContextValue {
    const ctx = useContext(AuthContext);
    if (!ctx) throw new Error('useAuth must be used within AuthProvider');
    return ctx;
}
