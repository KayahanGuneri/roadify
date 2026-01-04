/**
 * auth.ts
 *
 * English:
 * DTOs for Keycloak token responses.
 *
 * Türkçe Özet:
 * Keycloak token endpoint'inden dönen response tipi.
 */

export type TokenResponseDTO = {
    access_token: string;
    expires_in: number;
    refresh_expires_in?: number;
    refresh_token?: string;
    token_type: string;
    scope?: string;
};
