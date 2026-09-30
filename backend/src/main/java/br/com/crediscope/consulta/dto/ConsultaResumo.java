package br.com.crediscope.consulta.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.crediscope.consulta.Consulta;
import br.com.crediscope.consulta.Decisao;
import br.com.crediscope.consulta.TipoConsulta;
import br.com.crediscope.politica.FaixaRisco;
import br.com.crediscope.politica.Recomendacao;

/** Linha do histórico de consultas. */
public record ConsultaResumo(
        Long id,
        LocalDateTime criadoEm,
        String consultadoPor,
        String documento,
        String nome,
        TipoConsulta tipo,
        String finalidade,
        Integer score,
        FaixaRisco faixaRisco,
        Recomendacao recomendacao,
        Decisao decisao,
        BigDecimal custo) {

    public static ConsultaResumo de(Consulta c) {
        return new ConsultaResumo(c.getId(), c.getCriadoEm(),
                c.getUsuario() == null ? "Sistema" : c.getUsuario().getNome(),
                c.getCliente().getDocumento(), c.getCliente().getNome(), c.getTipo(), c.getFinalidade(),
                c.getScore(), c.getFaixaRisco(), c.getRecomendacao(), c.getDecisao(), c.getCusto());
    }
}
