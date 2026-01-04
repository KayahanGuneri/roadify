/**
 * jwt.ts
 *
 * English:
 * Minimal JWT helpers for decoding access token payload
 * and extracting a stable user identifier.
 *
 * Türkçe Özet:
 * Access token içinden payload decode edilir.
 * Öncelik sırası:
 * 1) preferred_username
 * 2) sub
 */

type JwtPayload = {
    sub?: string;
    preferred_username?: string;
    email?: string;
    [key: string]: unknown;
};

/**
 * Decode base64url-encoded string.
 */
function base64UrlDecode(input: string): string {
    const padded = input
        .replace(/-/g, '+')
        .replace(/_/g, '/')
        .padEnd(input.length + (4 - (input.length % 4)) % 4, '=');

    if (typeof atob !== 'function') {
        throw new Error('atob is not available in this environment');
    }

    try {
        return decodeURIComponent(
            Array.prototype.map
                .call(atob(padded), (c: string) =>
                    '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
                )
                .join('')
        );
    } catch (err) {
        throw new Error('Failed to decode base64url JWT payload');
    }
}

/**
 * Decode JWT payload safely.
 */
function decodeJwtPayload(accessToken: string): JwtPayload {
    const parts = accessToken.split('.');

    if (parts.length !== 3) {
        throw new Error('Invalid JWT format');
    }

    const payloadJson = base64UrlDecode(parts[1]);

    try {
        return JSON.parse(payloadJson) as JwtPayload;
    } catch {
        throw new Error('Invalid JWT payload JSON');
    }
}

/**
 * Extract user identifier from access token.
 *
 * Priority:
 * 1) preferred_username (Keycloak username)
 * 2) sub (UUID fallback)
 */
export function getUserIdFromAccessToken(accessToken: string): string {
    const payload = decodeJwtPayload(accessToken);

    if (
        payload.preferred_username &&
        typeof payload.preferred_username === 'string' &&
        payload.preferred_username.trim() !== ''
    ) {
        return payload.preferred_username;
    }

    if (
        payload.sub &&
        typeof payload.sub === 'string' &&
        payload.sub.trim() !== ''
    ) {
        return payload.sub;
    }

    throw new Error('No usable user identifier found in JWT');
}
