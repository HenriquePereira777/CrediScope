const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })
const moedaCurta = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL', maximumFractionDigits: 0 })

export const formatarMoeda = (valor) => (valor == null ? '—' : moeda.format(valor))
export const formatarMoedaCurta = (valor) => (valor == null ? '—' : moedaCurta.format(valor))

export const somenteNumeros = (valor = '') => String(valor).replace(/\D/g, '')

export function formatarDocumento(valor = '') {
  const n = somenteNumeros(valor)
  if (n.length === 11) return n.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
  if (n.length === 14) return n.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, '$1.$2.$3/$4-$5')
  return valor
}

/** Máscara progressiva enquanto o usuário digita. */
export function mascararDocumento(valor, tipo) {
  const n = somenteNumeros(valor).slice(0, tipo === 'PF' ? 11 : 14)
  if (tipo === 'PF') {
    return n
      .replace(/(\d{3})(\d)/, '$1.$2')
      .replace(/(\d{3})\.(\d{3})(\d)/, '$1.$2.$3')
      .replace(/(\d{3})\.(\d{3})\.(\d{3})(\d)/, '$1.$2.$3-$4')
  }
  return n
    .replace(/(\d{2})(\d)/, '$1.$2')
    .replace(/(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
    .replace(/(\d{2})\.(\d{3})\.(\d{3})(\d)/, '$1.$2.$3/$4')
    .replace(/(\d{2})\.(\d{3})\.(\d{3})\/(\d{4})(\d)/, '$1.$2.$3/$4-$5')
}

export function formatarDataHora(iso) {
  if (!iso) return '—'
  const d = new Date(iso)
  return d.toLocaleString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })
}

export function formatarDiaMes(isoData) {
  if (!isoData) return ''
  const [, m, d] = isoData.split('-')
  return `${d}/${m}`
}

export function iniciais(nome = '') {
  return nome
    .split(' ')
    .filter((p) => p.length > 2)
    .slice(0, 2)
    .map((p) => p[0])
    .join('')
    .toUpperCase()
}
