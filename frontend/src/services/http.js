import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

/**
 * Cliente HTTP único do sistema. Todos os services usam este objeto.
 * O cookie da sessão é enviado automaticamente pelo navegador (withCredentials).
 */
const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 30000,
  withCredentials: true,
})

// 401 fora das rotas de autenticação = sessão expirou ou foi encerrada → volta para o login
http.interceptors.response.use(
  (resposta) => resposta,
  (erro) => {
    const rotaDeAuth = erro.config?.url?.startsWith('/auth/')
    if (erro.response?.status === 401 && !rotaDeAuth) {
      const auth = useAuthStore()
      const tinhaSessao = !!auth.usuario
      auth.limpar()
      const atual = router.currentRoute.value
      if (atual.name !== 'login') {
        router.push({
          name: 'login',
          query: { voltar: atual.fullPath, ...(tinhaSessao ? { motivo: 'expirada' } : {}) },
        })
      }
    }
    return Promise.reject(erro)
  },
)

/** Extrai a mensagem de erro padrão da API ({ mensagem, detalhes }). */
export function mensagemDeErro(erro, padrao = 'Não foi possível concluir a operação.') {
  const dados = erro?.response?.data
  if (dados?.detalhes?.length) return `${dados.mensagem}: ${dados.detalhes.join(', ')}`
  if (dados?.mensagem) return dados.mensagem
  if (!erro?.response) return 'Sem conexão com o servidor. Verifique se o backend está rodando.'
  return padrao
}

export default http
