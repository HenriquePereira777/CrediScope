import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

/** Cliente HTTP único do sistema. Todos os services usam este objeto. */
const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 30000,
})

// Envia o token JWT em todas as requisições
http.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.token) config.headers.Authorization = `Bearer ${auth.token}`
  return config
})

// Sessão expirada → volta para o login
http.interceptors.response.use(
  (resposta) => resposta,
  (erro) => {
    const ehLogin = erro.config?.url?.includes('/auth/login')
    if (erro.response?.status === 401 && !ehLogin) {
      useAuthStore().sair()
      router.push({ name: 'login' })
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
