import { Alert, Linking } from 'react-native';

const KEYCLOAK_BASE_URL = 'http://10.0.2.2:8081';
const REALM = 'roadify';
const CLIENT_ID = 'roadify-mobile';
const REDIRECT_URI = 'roadify://auth/callback';

function randomString(len = 24) {
    const chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let out = '';
    for (let i = 0; i < len; i++) out += chars[Math.floor(Math.random() * chars.length)];
    return out;
}

async function openUrl(url: string) {
    try {
        await Linking.openURL(url);
    } catch {
        Alert.alert('Cannot open link', url);
    }
}

function buildAuthUrl(params: Record<string, string>) {
    const base = `${KEYCLOAK_BASE_URL}/realms/${REALM}/protocol/openid-connect/auth`;
    const qs = new URLSearchParams(params).toString();
    return `${base}?${qs}`;
}

export function openRegister() {
    const state = randomString();
    const nonce = randomString();

    const url = buildAuthUrl({
        client_id: CLIENT_ID,
        redirect_uri: REDIRECT_URI,
        response_type: 'code',
        scope: 'openid',
        kc_action: 'register',
        prompt: 'login',
        state,
        nonce,
    });

    return openUrl(url);
}

export function openForgotPassword() {
    const url =
        `${KEYCLOAK_BASE_URL}/realms/${REALM}/login-actions/reset-credentials` +
        `?client_id=${encodeURIComponent(CLIENT_ID)}` +
        `&redirect_uri=${encodeURIComponent(REDIRECT_URI)}`;

    return openUrl(url);
}
