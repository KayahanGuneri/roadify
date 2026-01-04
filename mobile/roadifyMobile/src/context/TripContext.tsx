/**
 * TripContext.tsx
 *
 * English:
 * Stores "currentTripId" for add-to-trip flow.
 *
 * Türkçe Özet:
 * Kullanıcının aktif tripId bilgisini uygulama içinde tutar.
 */

import React, { createContext, useContext, useMemo, useState } from 'react';

type TripContextValue = {
    currentTripId: string | null;
    setCurrentTripId: (id: string | null) => void;
};

const TripContext = createContext<TripContextValue | undefined>(undefined);

export const TripProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [currentTripId, setCurrentTripId] = useState<string | null>(null);

    const value = useMemo(
        () => ({ currentTripId, setCurrentTripId }),
        [currentTripId]
    );

    return <TripContext.Provider value={value}>{children}</TripContext.Provider>;
};

export function useTripContext(): TripContextValue {
    const ctx = useContext(TripContext);
    if (!ctx) {
        throw new Error('useTripContext must be used within TripProvider');
    }
    return ctx;
}
