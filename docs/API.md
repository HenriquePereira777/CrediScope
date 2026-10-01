# API · CrediScope
Base: `http://localhost:8080/api`. Todas as rotas, exceto as marcadas como **pública**, exigem estar logado.

**Como funciona a sessão:** o login grava o cookie `CREDISCOPE_SESSAO` (`HttpOnly`, `SameSite=Strict`, `Path=/api`).
O navegador envia esse cookie sozinho em todas as chamadas. O token **nunca** aparece no corpo das respostas
e o JavaScript da página não consegue lê-lo.

## Autenticação
| Método | Rota | Descrição |
|---|---|---|
| POST | `/auth/login` (pública) | `{ "email", "senha" }` → grava o cookie da sessão e devolve `{ usuario, expiraEm }` |
| GET | `/auth/me` | `{ usuario, expiraEm }` da sessão atual · 401 se não houver sessão válida |
| POST | `/auth/logout` (pública) | Apaga o cookie da sessão · responde 204 |

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
O `-c` salva o cookie num arquivo (como o navegador faria) e o `-b` envia esse cookie.

​```bash
# login (salva o cookie)
curl -c /tmp/cookie.txt -X POST localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@crediscope.local","senha":"admin123"}'

# chamada autenticada (envia o cookie)
curl -b /tmp/cookie.txt -X POST localhost:8080/api/consultas \
  -H "Content-Type: application/json" \
  -d '{"documento":"11.222.333/0001-81","tipo":"COMPLETO","finalidade":"Venda a prazo","monitorar":true}'

# sair
curl -b /tmp/cookie.txt -X POST localhost:8080/api/auth/logout
​```
