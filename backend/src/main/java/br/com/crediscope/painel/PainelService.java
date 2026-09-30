package br.com.crediscope.painel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.crediscope.cliente.ClienteRepository;
import br.com.crediscope.consulta.Consulta;
import br.com.crediscope.consulta.ConsultaRepository;
import br.com.crediscope.consulta.dto.ConsultaResumo;
import br.com.crediscope.painel.dto.ResumoPainel;
import br.com.crediscope.painel.dto.ResumoPainel.ConsultasDia;
import br.com.crediscope.painel.dto.ResumoPainel.RiscoCarteira;
import br.com.crediscope.politica.FaixaRisco;
import br.com.crediscope.politica.Recomendacao;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PainelService {

    private static final int DIAS_GRAFICO = 12;

    private final ConsultaRepository consultas;
    private final ClienteRepository clientes;

    @Transactional(readOnly = true)
    public ResumoPainel resumo() {
        LocalDate hoje = LocalDate.now();
        var inicioMes = hoje.withDayOfMonth(1).atStartOfDay();

        long noMes = consultas.countByCriadoEmGreaterThanEqual(inicioMes);
        long aprovadasMes = consultas.countByCriadoEmGreaterThanEqualAndRecomendacao(inicioMes, Recomendacao.APROVAR);
        int taxa = noMes == 0 ? 0 : (int) Math.round(aprovadasMes * 100.0 / noMes);

        RiscoCarteira risco = new RiscoCarteira(
                clientes.countByMonitoradoTrueAndFaixaRisco(FaixaRisco.BAIXO),
                clientes.countByMonitoradoTrueAndFaixaRisco(FaixaRisco.MEDIO),
                clientes.countByMonitoradoTrueAndFaixaRisco(FaixaRisco.ALTO));

        List<ConsultaResumo> ultimas = consultas.listarHistorico(PageRequest.of(0, 5))
                .map(ConsultaResumo::de).getContent();

        return new ResumoPainel(noMes, taxa, clientes.countByMonitoradoTrue(), risco,
                consultasPorDia(hoje), ultimas);
    }

    private List<ConsultasDia> consultasPorDia(LocalDate hoje) {
        LocalDate inicio = hoje.minusDays(DIAS_GRAFICO - 1L);
        Map<LocalDate, List<Consulta>> porDia = consultas.findByCriadoEmGreaterThanEqual(inicio.atStartOfDay())
                .stream()
                .collect(Collectors.groupingBy(c -> c.getCriadoEm().toLocalDate(), Collectors.mapping(Function.identity(), Collectors.toList())));

        List<ConsultasDia> resultado = new ArrayList<>();
        for (LocalDate dia = inicio; !dia.isAfter(hoje); dia = dia.plusDays(1)) {
            List<Consulta> doDia = porDia.getOrDefault(dia, List.of());
            long aprovadas = doDia.stream().filter(c -> c.getRecomendacao() == Recomendacao.APROVAR).count();
            resultado.add(new ConsultasDia(dia, doDia.size(), aprovadas));
        }
        return resultado;
    }
}
