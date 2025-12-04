// src/lib/httpClient.ts
import axios from 'axios';

const API_BASE_URL =
    __DEV__
        ? 'http://10.0.2.2:8082'         // ANDROID emulator -> PC backend
        : 'https://roadify-your-prod-url.com'; // ileride prod

export const httpClient = axios.create({
    baseURL: API_BASE_URL,
    timeout: 10000,
});
