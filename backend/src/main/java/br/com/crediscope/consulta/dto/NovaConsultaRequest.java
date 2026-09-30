package br.com.crediscope.consulta.dto;

import br.com.crediscope.consulta.TipoConsulta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NovaConsultaRequest(
        @NotBlank String documento,
        TipoConsulta tipo,
        @NotBlank @Size(max = 60) String finalidade,
        boolean monitorar) {

    public TipoConsulta tipoOuPadrao() {
        return tipo == null ? TipoConsulta.COMPLETO : tipo;
    }
}
