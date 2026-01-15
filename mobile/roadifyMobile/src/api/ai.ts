/**
 * ai.ts
 *
 * English:
 * API client for ai-assistant-service via gateway.
 *
 * Türkçe Özet:
 * Gateway üzerinden ai-assistant-service ile konuşan API fonksiyonları.
 */

import { gatewayClient } from './gatewayClient';
import type { AIChatRequest, AIChatResponse } from '../types/ai';

export async function callAiChat(request: AIChatRequest): Promise<AIChatResponse> {
    try {
        const res = await gatewayClient.post<AIChatResponse>('/mobile/v1/ai/chat', request);
        return res.data;
    } catch (error) {
        console.warn('[callAiChat][FAIL]', error);
        throw error;
    }
}
