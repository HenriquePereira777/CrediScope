# API · CrediScope

Base: `http://localhost:8080/api`. Todas as rotas, exceto as marcadas como **pública**, exigem o cabeçalho:

```
Authorization: Bearer <token>
```

## Autenticação
| Método | Rota | Descrição |
|---|---|---|
| POST | `/auth/login` (pública) | `{ "email", "senha" }` → `{ token, expiraEmMinutos, usuario }` |
| GET | `/auth/me` | Dados do usuário logado |

## Consultas
| Método | Rota | Descrição |
|---|---|---|
| POST | `/consultas` | Nova consulta: `{ "documento", "tipo": "COMPLETO\|SCORE\|CADASTRAL", "finalidade", "monitorar": true }` |
| GET | `/consultas/{id}` | Resultado completo (score, pendências, recomendação) |
| GET | `/consultas?pagina=0&tamanho=20` | Histórico paginado |
| PATCH | `/consultas/{id}/decisao` | `{ "decisao": "APROVADO\|RECUSADO\|EM_ANALISE" }` |

## Carteira
| Método | Rota | Descrição |
|---|---|---|
| GET | `/clientes?faixa=BAIXO\|MEDIO\|ALTO&pagina=0&tamanho=20` | Clientes monitorados |

## Painel
| Método | Rota | Descrição |
|---|---|---|
| GET | `/painel/resumo` | Indicadores, gráfico dos últimos 12 dias, risco da carteira, últimas consultas |

## Usuários
| Método | Rota | Perfil | Descrição |
|---|---|---|---|
| GET | `/usuarios` | Administrador, Gerente | Lista usuários |
| POST | `/usuarios` | Administrador | `{ "nome", "email", "senha", "perfil", "limiteAprovacao" }` |

## Outros
| Método | Rota | Descrição |
|---|---|---|
| GET | `/public/ping` (pública) | Teste se a API está no ar |
| GET | `/actuator/health` (pública) | Saúde da aplicação (usado pelo Docker) |

## Erros
Sempre no formato:
```json
{ "status": 422, "mensagem": "CNPJ inválido", "detalhes": [], "horario": "..." }
```
| Status | Quando |
|---|---|
| 400 | Dados inválidos (campos obrigatórios, formato) |
| 401 | Não logado, token expirado ou login inválido |
| 403 | Perfil sem permissão |
| 404 | Registro não encontrado |
| 422 | Regra de negócio (ex.: acima do limite de aprovação) |

## Testando pelo terminal
```bash
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login -H "Content-Type: application/json" \
  -d '{"email":"admin@crediscope.local","senha":"admin123"}' | jq -r .token)

curl -s -X POST localhost:8080/api/consultas -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"documento":"11.222.333/0001-81","tipo":"COMPLETO","finalidade":"Venda a prazo","monitorar":true}'
```
