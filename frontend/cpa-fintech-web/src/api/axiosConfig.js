import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080'
});

// 🛡️ El Interceptor: Antes de que salga cualquier petición...
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`; // 🔑 Aquí viaja tu llave
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

export default api;