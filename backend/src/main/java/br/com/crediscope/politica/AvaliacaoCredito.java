package br.com.crediscope.politica;

import java.math.BigDecimal;
import java.util.List;

/** Resultado da aplicação da política de crédito sobre um relatório. */
public record AvaliacaoCredito(
        FaixaRisco faixaRisco,
        Recomendacao recomendacao,
        BigDecimal limiteSugerido,
        List<String> motivos) {
}
