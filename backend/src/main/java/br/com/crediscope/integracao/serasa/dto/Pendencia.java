package br.com.crediscope.integracao.serasa.dto;

import java.math.BigDecimal;

/**
 * Uma pendência financeira encontrada.
 * tipo: RESTRICAO, PROTESTO, CHEQUE, ACAO_JUDICIAL ou FALENCIA
 */
public record Pendencia(
        String tipo,
        String descricao,
        int quantidade,
        BigDecimal valorTotal) {
}
