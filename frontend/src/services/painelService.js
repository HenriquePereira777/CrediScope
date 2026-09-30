import http from './http'

export default {
  resumo: () => http.get('/painel/resumo').then((r) => r.data),
}
