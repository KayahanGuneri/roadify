/**
 * ai.ts
 *
 * English:
 * DTOs and UI models for the AI assistant chat.
 *
 * Türkçe Özet:
 * AI asistan sohbeti için backend DTO tipleri ve UI modelleri.
 */

export type AISuggestionDTO = {
    placeId: string;
    name: string;
    shortReason: string;
};

export type AISuggestionFiltersDTO = {
    /**
     * Optional list of preferred categories (CAFE, FUEL, HOTEL, etc.).
     * Şimdilik kullanmıyoruz ama ileride filtre UI'si eklenebilir.
     */
    categories?: string[];
};

export type AIChatRequest = {
    routeId: string;
    message: string;
    filters?: AISuggestionFiltersDTO | null;
};

export type AIChatResponse = {
    /**
     * Natural language answer returned from backend (answer field).
     */
    answer: string;
    /**
     * Structured suggestions that UI can render as cards.
     */
    suggestions: AISuggestionDTO[];
};

export type ChatRole = 'user' | 'ai';

export type ChatMessage = {
    id: string;
    role: ChatRole;
    text: string;
    suggestions?: AISuggestionDTO[];
    createdAt: number;
};
