package br.com.crediscope.painel.dto;

import java.time.LocalDate;
import java.util.List;

import br.com.crediscope.consulta.dto.ConsultaResumo;

public record ResumoPainel(
        long consultasNoMes,
        int taxaAprovacao,
        long clientesMonitorados,
        RiscoCarteira riscoCarteira,
        List<ConsultasDia> consultasPorDia,
        List<ConsultaResumo> ultimasConsultas) {

    public record RiscoCarteira(long baixo, long medio, long alto) {
    }

    public record ConsultasDia(LocalDate dia, long total, long aprovadas) {
    }
}
