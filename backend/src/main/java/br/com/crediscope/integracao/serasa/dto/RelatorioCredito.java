package br.com.crediscope.integracao.serasa.dto;

import java.time.OffsetDateTime;
import java.util.List;

/** Resultado de uma consulta, já convertido para o formato interno do sistema. */
public record RelatorioCredito(
        String documento,
        String nome,
        String cidade,
        String uf,
        int score,
        List<Pendencia> pendencias,
        OffsetDateTime consultadoEm) {
}
