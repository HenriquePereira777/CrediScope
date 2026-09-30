import http from './http'

export default {
  login: (email, senha) => http.post('/auth/login', { email, senha }).then((r) => r.data),
  me: () => http.get('/auth/me').then((r) => r.data),
}
