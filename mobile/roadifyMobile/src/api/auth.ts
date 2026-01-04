// src/api/auth.ts
import axios from 'axios';
import type { TokenResponseDTO } from '../types/auth';
import { setAccessToken } from '../lib/tokenStore';

const KEYCLOAK_BASE_URL = 'http://10.0.2.2:8081';
const REALM = 'roadify';
const CLIENT_ID = 'roadify-mobile';

function decodeJwtPayload(token: string) {
    const payload = token.split('.')[1];
    const decoded = atob(
        payload.replace(/-/g, '+').replace(/_/g, '/')
    );
    return JSON.parse(decoded);
}

export async function loginWithPassword(
    username: string,
    password: string
): Promise<TokenResponseDTO> {
    const url = `${KEYCLOAK_BASE_URL}/realms/${REALM}/protocol/openid-connect/token`;

    const body = new URLSearchParams();
    body.append('grant_type', 'password');
    body.append('client_id', CLIENT_ID);
    body.append('username', username);
    body.append('password', password);

    const res = await axios.post<TokenResponseDTO>(url, body.toString(), {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        timeout: 10000,
    });

    // 🔍 DEBUG: token payload log
    const payload = decodeJwtPayload(res.data.access_token);
    console.log('[JWT payload]', {
        sub: payload.sub,
        preferred_username: payload.preferred_username,
        email: payload.email,
        full: payload,
    });
    console.log('[JWT iss]', payload.iss);
    await setAccessToken(res.data.access_token);

    return res.data;
}
