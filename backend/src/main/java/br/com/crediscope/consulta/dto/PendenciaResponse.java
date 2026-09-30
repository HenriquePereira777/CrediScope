package br.com.crediscope.consulta.dto;

import java.math.BigDecimal;

import br.com.crediscope.consulta.PendenciaConsulta;

public record PendenciaResponse(String tipo, String descricao, int quantidade, BigDecimal valorTotal) {

    public static PendenciaResponse de(PendenciaConsulta p) {
        return new PendenciaResponse(p.getTipo(), p.getDescricao(), p.getQuantidade(), p.getValorTotal());
    }
}
