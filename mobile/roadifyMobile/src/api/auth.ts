// src/api/auth.ts
import axios from 'axios';
import type { TokenResponseDTO } from '../types/auth';

const KEYCLOAK_BASE_URL = 'http://10.0.2.2:8081';
const REALM = 'roadify';
const CLIENT_ID = 'roadify-mobile';

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

    return res.data;
}
