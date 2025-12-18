import axios from 'axios';

/**
 * gatewayClient.ts
 *
 * English:
 * Axios client for Roadify API Gateway (mobile-facing entry point).
 *
 * Türkçe Özet:
 * Mobile uygulamanın gateway üzerinden backend'e istek atması için axios client.
 * Android emulator'da host makineye erişim için 10.0.2.2 kullanılır.
 */
export const gatewayClient = axios.create({
    baseURL: 'http://10.0.2.2:8080',
    timeout: 10000,
});
