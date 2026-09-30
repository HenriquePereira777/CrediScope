import http from './http'

export default {
  consultar: (dados) => http.post('/consultas', dados).then((r) => r.data),
  buscar: (id) => http.get(`/consultas/${id}`).then((r) => r.data),
  historico: (pagina = 0, tamanho = 20) => http.get('/consultas', { params: { pagina, tamanho } }).then((r) => r.data),
  decidir: (id, decisao) => http.patch(`/consultas/${id}/decisao`, { decisao }).then((r) => r.data),
}
