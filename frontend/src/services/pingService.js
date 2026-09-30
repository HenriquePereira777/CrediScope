import http from './http'

/** Testa se o backend está no ar (GET /api/public/ping). */
export default {
  ping: () => http.get('/public/ping').then((r) => r.data),
}
