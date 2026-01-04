import axios from 'axios';
import { getAccessToken } from '../lib/tokenStore';
import { getUserIdFromAccessToken } from '../lib/jwt';

export const gatewayClient = axios.create({
    baseURL: 'http://10.0.2.2:8080/api',
    timeout: 10000,
});

gatewayClient.interceptors.request.use(
    async (config) => {
        const token = await getAccessToken();
        console.log('[gatewayClient] token?', Boolean(token), 'url:', config.url);
        if (!token) return config;

        const userId = getUserIdFromAccessToken(token);

        // Axios v1: headers bazen AxiosHeaders instance olur.
        // Bu nedenle "set" varsa onu kullan, yoksa normal property set et.
        const headersAny = config.headers as any;

        if (typeof headersAny?.set === 'function') {
            headersAny.set('Authorization', `Bearer ${token}`);
            if (userId) headersAny.set('X-User-Id', userId);
        } else {
            // fallback: plain object ise
            (config.headers as any) = {
                ...(config.headers as any),
                Authorization: `Bearer ${token}`,
                ...(userId ? { 'X-User-Id': userId } : {}),
            };
        }

        return config;
    },
    (error) => Promise.reject(error)
);
