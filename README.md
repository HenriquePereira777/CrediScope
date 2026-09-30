# CrediScope · Sistema de Análise de Crédito

> Nome provisório. Para trocar, veja a seção **Renomear o projeto**.

## 1. Sobre o projeto

O **CrediScope** é um sistema web interno para **analisar clientes antes de vender a prazo**. Ele consulta CPF e CNPJ na **API da Serasa** e junta tudo num único painel: score, restrições, protestos, ações judiciais e dados cadastrais. Com isso, o time comercial e o financeiro decidem com segurança se aprovam o crédito e qual limite liberar.

### Problema que resolve
- A análise de crédito hoje é manual, lenta e cada pessoa decide de um jeito.
- Não existe histórico centralizado de quem consultou o quê.
- A empresa só descobre que um cliente piorou (nova dívida, protesto) quando ele já deixou de pagar.

### O que o sistema entrega
| Módulo | O que faz |
|---|---|
| **Login + 2 fatores** | Acesso por usuário, com código de verificação por e-mail ou aplicativo. |
| **Painel** | Indicadores do mês, gráfico de consultas e risco da carteira. |
| **Nova consulta** | Consulta de CPF/CNPJ na Serasa (relatório completo, score rápido ou cadastral). |
| **Resultado da análise** | Score, pendências, dados cadastrais, evolução do score e recomendação automática. |
| **Carteira de clientes** | Clientes monitorados, atualizados todos os dias. |
| **Alertas** | Aviso quando um cliente ganha restrição ou o score cai. |
| **Histórico** | Registro de todas as consultas: quem, quando, por quê e quanto custou (LGPD). |
| **Política de crédito** | Faixas de score e regras automáticas de aprovação e recusa. |
| **Usuários** | Perfis (Administrador, Gerente, Analista, Vendedor) e limite de aprovação de cada um. |
| **Integrações** | Conexão com a Serasa e importação de clientes do ERP. |

Os layouts de referência de cada tela estão em [`docs/telas/`](docs/telas).

### Equipe
| Nome | Papel |
|---|---|
| Jonatan Alves | Desenvolvimento |
| Henrique Pereira | Engenheiro de Software |

---

## 2. Tecnologias

| Camada | Tecnologia |
|---|---|
| Backend | Java 21 · Spring Boot 4.1 · Spring Security · Spring Data JPA · Bean Validation |
| Banco de dados | PostgreSQL 17 · Flyway (versionamento do banco) |
| Frontend | Vue 3 · Vite · Vue Router · Pinia · Axios · Tailwind CSS 4 · ícones Lucide |
| Infraestrutura | Docker · Docker Compose · Nginx |

---

## 3. Estrutura de pastas

```
crediscope/
├── backend/                         API REST (Spring Boot)
│   ├── src/main/java/br/com/crediscope/
│   │   ├── CrediScopeApplication.java
│   │   ├── config/                  Segurança, CORS
│   │   ├── shared/                  Código comum a todos os módulos
│   │   │   ├── api/                 Tratamento de erros, ping
│   │   │   ├── exception/           Exceções de negócio
│   │   │   └── util/                Validação de CPF/CNPJ etc.
│   │   ├── auth/                    Login, JWT, 2 fatores
│   │   ├── usuario/                 Usuários e permissões
│   │   ├── cliente/                 Carteira de clientes
│   │   ├── consulta/                Consultas e resultado
│   │   ├── politica/                Política de crédito
│   │   ├── alerta/                  Alertas do monitoramento
│   │   ├── auditoria/               Histórico / LGPD
│   │   ├── painel/                  Indicadores do painel
│   │   └── integracao/serasa/       Integração com a Serasa (real e mock)
│   ├── src/main/resources/
│   │   ├── application.yml          Configuração principal
│   │   ├── application-dev.yml      Configuração de desenvolvimento
│   │   ├── application-prod.yml     Configuração de produção
│   │   └── db/migration/            Scripts do banco (Flyway)
│   ├── src/test/java/...            Testes
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                        Aplicação web (Vue 3)
│   ├── src/
│   │   ├── assets/main.css          Tailwind + cores do sistema
│   │   ├── components/
│   │   │   ├── layout/              Menu lateral, barra superior, logo
│   │   │   └── ui/                  Componentes reutilizáveis
│   │   ├── layouts/                 AuthLayout (login) e AppLayout (sistema)
│   │   ├── router/                  Rotas e proteção de login
│   │   ├── services/                Chamadas à API (axios)
│   │   ├── stores/                  Estado global (Pinia)
│   │   ├── utils/                   Formatadores (moeda, CPF/CNPJ)
│   │   └── views/                   Uma pasta por tela
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
├── docs/
│   ├── ARQUITETURA.md               Decisões técnicas e padrões
│   ├── API.md                       Lista de endpoints
│   └── telas/                       Layouts de referência (PNG)
│
├── docker-compose.yml               Banco + API + Web
├── .env.example                     Modelo das variáveis de ambiente
└── README.md
```

O backend é organizado **por módulo de negócio** (`consulta`, `cliente`, `usuario`...), e não por camada técnica. Tudo o que é de uma funcionalidade fica junto: controller, service, repository, entidade e DTOs. Mais detalhes em [`docs/ARQUITETURA.md`](docs/ARQUITETURA.md).

---

## 4. Como rodar

### Pré-requisitos
- Java 21 (JDK)
- Maven 3.9 ou superior
- Node.js 22 ou superior
- Docker Desktop

### Passo a passo (desenvolvimento)

```bash
# 1. Variáveis de ambiente
cp .env.example .env

# 2. Subir só o banco
docker compose up -d postgres

# 3. Backend  →  http://localhost:8080
cd backend
mvn spring-boot:run

# 4. Frontend (em outro terminal)  →  http://localhost:5173
cd frontend
npm install
npm run dev
```

Para testar se a API está no ar, acesse `GET http://localhost:8080/api/public/ping`.

**Primeiro acesso**
- Usuário: `admin@crediscope.local`
- Senha: `admin123` (criado pela migration `V2`; troque antes de ir para produção)

**Para testar uma consulta** (Serasa em modo simulado), use documentos com dígito verificador válido, por exemplo:
- CNPJ `11.222.333/0001-81`
- CPF `529.982.247-25`

O mesmo documento sempre gera o mesmo resultado fictício.

### Tudo no Docker (como em produção)

```bash
docker compose up -d --build
# Web: http://localhost:8081   ·   API: http://localhost:8080
```

---

## 5. Integração com a Serasa

- A API da Serasa **exige contrato comercial**. As credenciais (`client-id` e `client-secret`) só são liberadas depois que o contrato é assinado.
- Enquanto isso, o sistema roda com `SERASA_MODO=mock`, que devolve **dados fictícios**. O desenvolvimento não precisa esperar o contrato.
- Quando as credenciais chegarem, basta implementar o `SerasaApiClient` (a mesma interface `SerasaClient`) e trocar a variável para `SERASA_MODO=api`.
- **Credenciais nunca vão para o Git.** Ficam só no arquivo `.env` ou no servidor.

## 6. LGPD

Consultar CPF para conceder crédito é permitido pela LGPD (art. 7º, X – proteção do crédito), desde que haja controle. Por isso o sistema:
- exige login individual (nada de usuário compartilhado);
- registra toda consulta com usuário, data/hora, finalidade e IP;
- limita, por perfil, quem pode consultar e aprovar.

---

## 7. Padrões da equipe

**Branches**
- `main` – versão em produção (protegida)
- `develop` – integração do que está pronto
- `feature/nome-curto` – cada funcionalidade (ex.: `feature/tela-login`)
- `fix/nome-curto` – correções

**Commits** (em português, no imperativo)
```
feat: cria tela de nova consulta
fix: corrige validação de CNPJ
refactor: separa regras da política de crédito
docs: atualiza README
```

**Banco de dados**
- Toda mudança no banco é uma **nova migration** (`V2__descricao.sql`, `V3__...`).
- Nunca edite uma migration que já rodou.

**Código**
- Nomes de negócio em português (`Consulta`, `Cliente`, `limiteSugerido`).
- Um arquivo `.vue` por tela em `views/<modulo>/`; partes reutilizáveis vão para `components/`.
- Toda chamada à API passa pelos arquivos de `services/`, nunca direto no componente.

---

## 8. Roteiro de desenvolvimento

| Fase | Entregas | Status |
|---|---|---|
| 0 · Estrutura | Pastas, Docker, banco inicial, layout base, menu | ✅ Concluída |
| 1 · Acesso | Login com JWT ✅ · perfis ✅ · API de usuários ✅ · tela de usuários ⬜ · 2 fatores ⬜ | 🟡 Em andamento |
| 2 · Consulta | Nova consulta (mock) ✅ · resultado ✅ · aprovar/recusar ✅ · histórico ✅ · consulta em lote ⬜ | 🟡 Em andamento |
| 3 · Política | Regras fixas no código ✅ · tela para editar as regras (tabela `regra_politica`) ⬜ | 🟡 Em andamento |
| 4 · Carteira | Lista da carteira ✅ · atualização diária automática ⬜ · alertas ⬜ | 🟡 Em andamento |
| 5 · Painel | Indicadores e gráficos ✅ · filtros e exportação do histórico ⬜ | 🟡 Em andamento |
| 6 · Serasa real | `SerasaApiClient` com as credenciais do contrato, testes em homologação | ⬜ |
| 7 · ERP | Importação de clientes do ERP da empresa | ⬜ |
| 8 · Produção | Deploy, backup do banco, HTTPS, monitoramento | ⬜ |

---

## Renomear o projeto

"CrediScope" é provisório. Para trocar:
1. O pacote Java `br.com.crediscope` (renomeie pela IDE).
2. O `groupId` e o `artifactId` no `backend/pom.xml`.
3. O `name` no `frontend/package.json`, o `<title>` no `index.html` e o componente `AppLogo.vue`.
4. Os nomes dos containers e do banco no `docker-compose.yml` e no `.env`.
