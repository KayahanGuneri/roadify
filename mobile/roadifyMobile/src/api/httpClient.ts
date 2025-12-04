import axios from 'axios';

// Android emulator -> host
export const httpClient = axios.create({
    baseURL: 'http://10.0.2.2:8082', // direct route-service for now
    timeout: 10000,
});
