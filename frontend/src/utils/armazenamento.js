/**
 * Acesso seguro ao localStorage.
 * Em janelas anônimas ou com o armazenamento bloqueado, o navegador pode lançar erro;
 * aqui o erro é ignorado e o sistema continua funcionando (só não lembra os dados).
 */
export const armazenamento = {
  ler(chave) {
    try {
      return localStorage.getItem(chave)
    } catch {
      return null
    }
  },
  gravar(chave, valor) {
    try {
      localStorage.setItem(chave, valor)
    } catch {
      /* sem armazenamento disponível */
    }
  },
  remover(chave) {
    try {
      localStorage.removeItem(chave)
    } catch {
      /* sem armazenamento disponível */
    }
  },
}
