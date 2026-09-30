package br.com.crediscope.cliente.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.crediscope.cliente.Cliente;
import br.com.crediscope.politica.FaixaRisco;

public record ClienteResponse(
        Long id,
        String tipoPessoa,
        String documento,
        String nome,
        String cidade,
        String uf,
        Integer scoreAtual,
        FaixaRisco faixaRisco,
        BigDecimal limiteConcedido,
        BigDecimal valorEmAberto,
        boolean monitorado,
        LocalDateTime atualizadoScoreEm) {

    public static ClienteResponse de(Cliente c) {
        return new ClienteResponse(c.getId(), c.getTipoPessoa(), c.getDocumento(), c.getNome(),
                c.getCidade(), c.getUf(), c.getScoreAtual(), c.getFaixaRisco(), c.getLimiteConcedido(),
                c.getValorEmAberto(), c.isMonitorado(), c.getAtualizadoScoreEm());
    }
}
