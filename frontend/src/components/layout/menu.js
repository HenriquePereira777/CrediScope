import {
  Bell, ChartLine, Clock, House, Layers, Plug, Search, Shield, SlidersHorizontal, User, Users,
} from 'lucide-vue-next'

/** Itens do menu lateral. Para criar uma tela nova, adicione aqui e no router. */
export const menu = [
  {
    grupo: 'PRINCIPAL',
    itens: [
      { rotulo: 'Painel', rota: 'painel', icone: House },
      { rotulo: 'Nova consulta', rota: 'nova-consulta', icone: Search },
      { rotulo: 'Consulta em lote', rota: 'consulta-lote', icone: Layers, selo: { texto: 'Novo', tipo: 'novo' } },
      { rotulo: 'Carteira de clientes', rota: 'carteira', icone: Users },
      { rotulo: 'Alertas', rota: 'alertas', icone: Bell },
      { rotulo: 'Histórico', rota: 'historico', icone: Clock },
      { rotulo: 'Relatórios', rota: 'relatorios', icone: ChartLine },
    ],
  },
  {
    grupo: 'GESTÃO',
    itens: [
      { rotulo: 'Política de crédito', rota: 'politica', icone: Shield },
      { rotulo: 'Usuários', rota: 'usuarios', icone: User },
      { rotulo: 'Integrações', rota: 'integracoes', icone: Plug },
      { rotulo: 'Configurações', rota: 'configuracoes', icone: SlidersHorizontal },
    ],
  },
]
