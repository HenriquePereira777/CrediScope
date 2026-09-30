package br.com.crediscope.consulta.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.crediscope.cliente.Cliente;
import br.com.crediscope.consulta.Consulta;
import br.com.crediscope.consulta.Decisao;
import br.com.crediscope.consulta.TipoConsulta;
import br.com.crediscope.politica.FaixaRisco;
import br.com.crediscope.politica.Recomendacao;

/** Resultado completo de uma consulta (tela Resultado da análise). */
public record ConsultaResponse(
        Long id,
        Long clienteId,
        String tipoPessoa,
        String documento,
        String nome,
        String cidade,
        String uf,
        TipoConsulta tipo,
        String finalidade,
        Integer score,
        FaixaRisco faixaRisco,
        Recomendacao recomendacao,
        Decisao decisao,
        BigDecimal limiteSugerido,
        BigDecimal custo,
        String consultadoPor,
        LocalDateTime criadoEm,
        List<PendenciaResponse> pendencias) {

    public static ConsultaResponse de(Consulta c) {
        Cliente cli = c.getCliente();
        return new ConsultaResponse(
                c.getId(), cli.getId(), cli.getTipoPessoa(), cli.getDocumento(), cli.getNome(),
                cli.getCidade(), cli.getUf(), c.getTipo(), c.getFinalidade(), c.getScore(),
                c.getFaixaRisco(), c.getRecomendacao(), c.getDecisao(), c.getLimiteSugerido(),
                c.getCusto(), c.getUsuario() == null ? "Sistema" : c.getUsuario().getNome(),
                c.getCriadoEm(), c.getPendencias().stream().map(PendenciaResponse::de).toList());
    }
}
