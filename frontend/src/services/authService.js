import http from './http'

/**
 * Autenticação. O token fica num cookie HttpOnly gravado pelo backend:
 * o frontend nunca vê nem guarda o token.
 */
export default {
  /** → { usuario, expiraEm } */
  login: (email, senha) => http.post('/auth/login', { email, senha }).then((r) => r.data),
  /** → { usuario, expiraEm } se a sessão (cookie) for válida; 401 se não for */
  me: () => http.get('/auth/me').then((r) => r.data),
  /** Pede ao backend para apagar o cookie da sessão */
  logout: () => http.post('/auth/logout'),
}
