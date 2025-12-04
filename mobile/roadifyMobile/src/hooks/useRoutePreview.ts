// src/hooks/useRoutePreview.ts
import { useMutation } from '@tanstack/react-query';
import type { RoutePreviewRequestDTO, RouteDTO } from '../types/routes';

const BASE_URL = 'http://10.0.2.2:8082';

async function postRoutePreview(body: RoutePreviewRequestDTO): Promise<RouteDTO> {
    const response = await fetch(`${BASE_URL}/v1/routes/preview`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(body),
    });

    if (!response.ok) {
        const text = await response.text().catch(() => '');
        console.log('Route preview error:', response.status, text);
        throw new Error(
            `Route preview failed (${response.status}) ${
                text || response.statusText
            }`,
        );
    }

    return (await response.json()) as RouteDTO;
}

export function useRoutePreview(opts?: { onSuccess?: (route: RouteDTO) => void }) {
    return useMutation({
        mutationFn: postRoutePreview,
        ...opts,
    });
}
