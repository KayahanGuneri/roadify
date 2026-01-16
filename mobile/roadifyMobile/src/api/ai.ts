/**
 * ai.ts
 *
 * English:
 * API client for ai-assistant-service via gateway.
 *
 * Türkçe Özet:
 * Gateway üzerinden ai-assistant-service ile konuşan API fonksiyonları.
 */

// mobile/roadifyMobile/src/api/ai.ts
import { gatewayClient } from './gatewayClient';
import type { AIChatRequest, AIChatResponse } from '../types/ai';

/**
 * Call AI assistant chat endpoint via gateway.
 *
 * POST /api/mobile/v1/ai/chat -> gateway -> /v1/ai/chat (ai-assistant-service)
 */
export async function callAiChat(
    request: AIChatRequest,
): Promise<AIChatResponse> {
    try {
        const res = await gatewayClient.post<AIChatResponse>(
            '/mobile/v1/ai/chat',
            request,
            {
                // AI için özel timeout (örnek: 60 saniye)
                timeout: 60000,
            },
        );

        console.log(
            '[callAiChat][OK]',
            res.status,
            JSON.stringify(res.data).slice(0, 200),
        );
        return res.data;
    } catch (e: any) {
        if (e?.response) {
            console.log('[callAiChat][FAIL][HTTP]', {
                status: e.response.status,
                data: e.response.data,
                url: e.config?.url,
            });
        } else if (e?.request) {
            console.log('[callAiChat][FAIL][NO_RESPONSE]', {
                message: e.message,
                code: e.code,
                url: e.config?.url,
            });
        } else {
            console.log('[callAiChat][FAIL][OTHER]', {
                message: e?.message,
                code: e?.code,
            });
        }
        throw e;
    }
}
