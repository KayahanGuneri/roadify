/**
 * useAiChat.ts
 *
 * English:
 * Hook that manages AI chat state and calls the backend.
 *
 * Türkçe Özet:
 * AI sohbet state'ini yöneten ve backend'e istek atan hook.
 */

import { useMutation } from '@tanstack/react-query';
import { useCallback, useState } from 'react';
import { callAiChat } from '../api/ai';
import type {
    AIChatRequest,
    AISuggestionFiltersDTO,
    ChatMessage,
} from '../types/ai';

type UseAiChatOptions = {
    routeId: string | null;
    filters?: AISuggestionFiltersDTO | null;
};

type UseAiChatResult = {
    messages: ChatMessage[];
    isSending: boolean;
    error: Error | null;
    sendMessage: (text: string) => Promise<void>;
};

export function useAiChat(options: UseAiChatOptions): UseAiChatResult {
    const { routeId, filters = null } = options;
    const [messages, setMessages] = useState<ChatMessage[]>([]);

    const chatMutation = useMutation({
        mutationFn: async (messageText: string) => {
            if (!routeId) {
                throw new Error('Missing routeId for AI chat');
            }

            const req: AIChatRequest = {
                routeId,
                message: messageText,
                filters: filters ?? undefined,
            };

            return callAiChat(req);
        },
    });

    const sendMessage = useCallback(
        async (text: string) => {
            const trimmed = text.trim();
            if (!trimmed) return;

            const now = Date.now();

            const userMessage: ChatMessage = {
                id: `user-${now}`,
                role: 'user',
                text: trimmed,
                createdAt: now,
            };

            // Önce kullanıcı mesajını ekleyelim
            setMessages((prev) => [...prev, userMessage]);

            try {
                const response = await chatMutation.mutateAsync(trimmed);

                const aiMessage: ChatMessage = {
                    id: `ai-${Date.now()}`,
                    role: 'ai',
                    text: response.answer,
                    suggestions: response.suggestions ?? [],
                    createdAt: Date.now(),
                };

                setMessages((prev) => [...prev, aiMessage]);
            } catch (e) {
                const errorMessage: ChatMessage = {
                    id: `ai-error-${Date.now()}`,
                    role: 'ai',
                    text: 'Sorry, I could not generate suggestions right now. Please try again.',
                    createdAt: Date.now(),
                };

                setMessages((prev) => [...prev, errorMessage]);
            }
        },
        [chatMutation],
    );

    return {
        messages,
        isSending: chatMutation.isPending,
        error: (chatMutation.error as Error) ?? null,
        sendMessage,
    };
}
