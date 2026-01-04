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

type AuthContextValue = {
    accessToken: string | null;
    login: (username: string, password: string) => Promise<void>;
    logout: () => void;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [accessToken, setAccessToken] = useState<string | null>(null);

    const login = async (username: string, password: string) => {
        const token = await loginWithPassword(username, password);
        setAccessToken(token.access_token);
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
