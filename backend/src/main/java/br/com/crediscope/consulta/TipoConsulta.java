package br.com.crediscope.consulta;

import java.math.BigDecimal;

/** Tipos de relatório e custo de cada um (valores de exemplo; ajustar conforme o contrato). */
public enum TipoConsulta {
    COMPLETO(new BigDecimal("8.90")),
    SCORE(new BigDecimal("2.50")),
    CADASTRAL(new BigDecimal("1.80")),
    MONITORAMENTO(BigDecimal.ZERO);

    private final BigDecimal custo;

    TipoConsulta(BigDecimal custo) {
        this.custo = custo;
    }

    public BigDecimal getCusto() {
        return custo;
    }
}
