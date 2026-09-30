import http from './http'

export default {
  carteira: (faixa = null, pagina = 0, tamanho = 20) =>
    http.get('/clientes', { params: { faixa: faixa || undefined, pagina, tamanho } }).then((r) => r.data),
}
