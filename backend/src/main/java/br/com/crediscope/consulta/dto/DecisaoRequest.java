package br.com.crediscope.consulta.dto;

import br.com.crediscope.consulta.Decisao;
import jakarta.validation.constraints.NotNull;

public record DecisaoRequest(@NotNull Decisao decisao) {
}
