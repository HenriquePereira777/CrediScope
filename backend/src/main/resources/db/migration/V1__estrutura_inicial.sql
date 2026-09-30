-- ============================================================
-- CrediScope · Estrutura inicial do banco (proposta v1)
-- Ajustes no esquema devem ser feitos SEMPRE em uma nova
-- migration (V2__..., V3__...), nunca editando esta.
-- ============================================================

-- Usuários do sistema
CREATE TABLE usuario (
    id                BIGSERIAL PRIMARY KEY,
    nome              VARCHAR(120)  NOT NULL,
    email             VARCHAR(160)  NOT NULL UNIQUE,
    senha_hash        VARCHAR(255)  NOT NULL,
    perfil            VARCHAR(30)   NOT NULL,          -- ADMINISTRADOR, GERENTE, ANALISTA, VENDEDOR
    limite_aprovacao  NUMERIC(14,2),                   -- NULL = sem limite
    dois_fatores      BOOLEAN       NOT NULL DEFAULT FALSE,
    ativo             BOOLEAN       NOT NULL DEFAULT TRUE,
    ultimo_acesso     TIMESTAMP,
    criado_em         TIMESTAMP     NOT NULL DEFAULT NOW(),
    atualizado_em     TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- Clientes analisados / monitorados (carteira)
CREATE TABLE cliente (
    id                  BIGSERIAL PRIMARY KEY,
    tipo_pessoa         CHAR(2)       NOT NULL,          -- PF ou PJ
    documento           VARCHAR(14)   NOT NULL UNIQUE,   -- CPF/CNPJ só números
    nome                VARCHAR(200)  NOT NULL,
    cidade              VARCHAR(100),
    uf                  CHAR(2),
    score_atual         INTEGER,
    faixa_risco         VARCHAR(10),                     -- BAIXO, MEDIO, ALTO
    limite_concedido    NUMERIC(14,2),
    valor_em_aberto     NUMERIC(14,2),
    monitorado          BOOLEAN       NOT NULL DEFAULT FALSE,
    codigo_erp          VARCHAR(50),                     -- vínculo com o ERP da empresa
    atualizado_score_em TIMESTAMP,
    criado_em           TIMESTAMP     NOT NULL DEFAULT NOW(),
    atualizado_em       TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- Cada consulta feita (também serve de trilha de auditoria / LGPD)
CREATE TABLE consulta (
    id              BIGSERIAL PRIMARY KEY,
    cliente_id      BIGINT        NOT NULL REFERENCES cliente(id),
    usuario_id      BIGINT        REFERENCES usuario(id),  -- NULL = consulta automática do sistema
    tipo            VARCHAR(20)   NOT NULL,                -- COMPLETO, SCORE, CADASTRAL, MONITORAMENTO
    finalidade      VARCHAR(60)   NOT NULL,                -- ex.: VENDA_A_PRAZO, CREDIARIO
    score           INTEGER,
    faixa_risco     VARCHAR(10),
    recomendacao    VARCHAR(20),                           -- APROVAR, ANALISE_MANUAL, RECUSAR
    decisao         VARCHAR(20),                           -- APROVADO, RECUSADO, EM_ANALISE
    limite_sugerido NUMERIC(14,2),
    custo           NUMERIC(8,2)  NOT NULL DEFAULT 0,
    resposta_json   JSONB,                                 -- retorno bruto da Serasa
    ip_origem       VARCHAR(45),
    criado_em       TIMESTAMP     NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_consulta_cliente   ON consulta (cliente_id);
CREATE INDEX idx_consulta_usuario   ON consulta (usuario_id);
CREATE INDEX idx_consulta_criado_em ON consulta (criado_em);

-- Pendências encontradas em cada consulta
CREATE TABLE pendencia (
    id           BIGSERIAL PRIMARY KEY,
    consulta_id  BIGINT        NOT NULL REFERENCES consulta(id) ON DELETE CASCADE,
    tipo         VARCHAR(30)   NOT NULL,   -- RESTRICAO, PROTESTO, CHEQUE, ACAO_JUDICIAL, FALENCIA
    descricao    VARCHAR(255),
    quantidade   INTEGER       NOT NULL DEFAULT 0,
    valor_total  NUMERIC(14,2)
);

-- Regras da política de crédito
CREATE TABLE regra_politica (
    id          BIGSERIAL PRIMARY KEY,
    descricao   VARCHAR(200) NOT NULL,
    tipo        VARCHAR(40)  NOT NULL,   -- FAIXA_SCORE, PROTESTO_MAXIMO, ACAO_JUDICIAL, LIMITE_FATURAMENTO...
    parametros  JSONB        NOT NULL DEFAULT '{}'::jsonb,
    ativa       BOOLEAN      NOT NULL DEFAULT TRUE,
    ordem       INTEGER      NOT NULL DEFAULT 0,
    criado_em   TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Alertas do monitoramento da carteira
CREATE TABLE alerta (
    id          BIGSERIAL PRIMARY KEY,
    cliente_id  BIGINT       NOT NULL REFERENCES cliente(id),
    tipo        VARCHAR(30)  NOT NULL,   -- NOVA_RESTRICAO, QUEDA_SCORE, PENDENCIA_QUITADA
    mensagem    VARCHAR(255) NOT NULL,
    lido        BOOLEAN      NOT NULL DEFAULT FALSE,
    criado_em   TIMESTAMP    NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_alerta_nao_lido ON alerta (lido) WHERE lido = FALSE;
