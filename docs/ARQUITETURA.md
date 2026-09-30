# Arquitetura · CrediScope

## Visão geral

```
 Navegador ──► Frontend (Vue 3 + Nginx) ──/api──► Backend (Spring Boot) ──► PostgreSQL
                                                        │
                                                        └──► API Serasa (ou mock)
                                                        └──► ERP da empresa (futuro)
```

- O **frontend** só fala com o nosso backend, nunca direto com a Serasa. As credenciais ficam protegidas no servidor.
- O **backend** é uma API REST stateless: cada requisição leva um token JWT.
- O **banco** é versionado pelo Flyway, então todo mundo tem sempre a mesma estrutura.

## Backend: organização por módulo

Cada pasta de negócio segue o mesmo formato. Exemplo com `consulta`:

```
consulta/
├── ConsultaController.java      POST /api/consultas, GET /api/consultas/{id}
├── ConsultaService.java         regras: valida documento, chama Serasa, aplica política, grava histórico
├── ConsultaRepository.java      interface Spring Data JPA
├── Consulta.java                @Entity (tabela consulta)
└── dto/
    ├── NovaConsultaRequest.java record com @Valid
    └── ConsultaResponse.java    record devolvido ao frontend
```

**Regras:**
- O controller não tem regra de negócio: recebe, valida (`@Valid`) e chama o service.
- A entidade (`@Entity`) nunca sai da API. O que sai são sempre DTOs (`record`).
- Um módulo usa outro através do **service** do outro, nunca do repository.
- Erros de negócio lançam `NegocioException`. O `GlobalExceptionHandler` transforma em JSON padrão.

## Padrão de resposta de erro

```json
{
  "status": 422,
  "mensagem": "Limite de consultas do plano atingido",
  "detalhes": [],
  "horario": "2026-09-27T17:30:00-03:00"
}
```

## Integração Serasa: padrão "porta e adaptador"

```
ConsultaService ──► SerasaClient (interface)
                        ├── SerasaMockClient   (serasa.modo=mock) ✅ pronto
                        └── SerasaApiClient    (serasa.modo=api)  ⬜ implementar quando houver contrato
```

A troca é feita só por configuração, sem mexer no resto do código.

## Frontend

| Pasta | Responsabilidade |
|---|---|
| `views/` | Uma tela por arquivo, agrupadas por módulo |
| `components/layout/` | Menu lateral (`menu.js` define os itens), barra superior |
| `components/ui/` | Peças reutilizáveis (cabeçalho de página, cartões, tabelas, badges) |
| `services/` | Chamadas HTTP; `http.js` injeta o token e trata o 401 |
| `stores/` | Estado global com Pinia (sessão do usuário) |
| `router/` | Rotas; `meta.layout` escolhe o layout, `meta.publica` libera sem login |

**Cores e fonte** ficam em `src/assets/main.css` (bloco `@theme`) e seguem os layouts de `docs/telas`:
primária `#6D5DFC`, destaque `#4F8CFF`, verde-água `#12B5A6`, fonte *Plus Jakarta Sans*.

## Banco de dados (v1)

| Tabela | Uso |
|---|---|
| `usuario` | Usuários, perfil e limite de aprovação |
| `cliente` | Clientes analisados/monitorados (carteira) |
| `consulta` | Cada consulta feita, também usada como trilha de auditoria (LGPD) |
| `pendencia` | Pendências encontradas em cada consulta |
| `regra_politica` | Regras da política de crédito |
| `alerta` | Alertas do monitoramento |

Script: `backend/src/main/resources/db/migration/V1__estrutura_inicial.sql`

## Fluxo de uma consulta

```
Tela Nova consulta ─POST /api/consultas─► ConsultaService
   1. valida CPF/CNPJ (DocumentoUtil)
   2. SerasaClient.consultarCnpj/Cpf
   3. PoliticaCreditoService.avaliar → faixa de risco, recomendação, limite sugerido
   4. cria/atualiza o Cliente (carteira)
   5. grava Consulta + Pendências (histórico / LGPD)
◄── ConsultaResponse ── tela Resultado da análise
```

## Segurança

- Senhas com BCrypt.
- JWT (HS256) gerado no login e validado pelo Spring Security (Resource Server), com expiração configurável (`JWT_EXPIRACAO_MINUTOS`).
- O perfil do usuário vai no token e vira permissão (`@PreAuthorize("hasRole('ADMINISTRADOR')")`).
- 2 fatores por código de 6 dígitos (a implementar).
- CORS liberado só para a URL do frontend (`CORS_ORIGINS`).
- Segredos só em variáveis de ambiente (`.env`), nunca no código.
