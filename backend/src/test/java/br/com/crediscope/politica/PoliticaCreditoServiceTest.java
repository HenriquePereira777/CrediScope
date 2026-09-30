package br.com.crediscope.politica;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.crediscope.integracao.serasa.dto.Pendencia;
import br.com.crediscope.integracao.serasa.dto.RelatorioCredito;

class PoliticaCreditoServiceTest {

    private final PoliticaCreditoService politica = new PoliticaCreditoService();

    private RelatorioCredito relatorio(int score, Pendencia... pendencias) {
        return new RelatorioCredito("11222333000181", "Teste", "Pelotas", "RS", score, List.of(pendencias), OffsetDateTime.now());
    }

    @Test
    void scoreAltoSemPendenciasDeveAprovar() {
        AvaliacaoCredito a = politica.avaliar(relatorio(842));
        assertEquals(FaixaRisco.BAIXO, a.faixaRisco());
        assertEquals(Recomendacao.APROVAR, a.recomendacao());
        assertEquals(new BigDecimal("24000"), a.limiteSugerido());
    }

    @Test
    void scoreMedioDeveIrParaAnaliseManual() {
        assertEquals(Recomendacao.ANALISE_MANUAL, politica.avaliar(relatorio(611)).recomendacao());
    }

    @Test
    void scoreBaixoDeveRecusar() {
        AvaliacaoCredito a = politica.avaliar(relatorio(420));
        assertEquals(Recomendacao.RECUSAR, a.recomendacao());
        assertEquals(BigDecimal.ZERO, a.limiteSugerido());
    }

    @Test
    void protestoAcimaDe5MilDeveRecusarMesmoComScoreBom() {
        Pendencia protesto = new Pendencia("PROTESTO", "Protesto", 1, new BigDecimal("6000"));
        assertEquals(Recomendacao.RECUSAR, politica.avaliar(relatorio(800, protesto)).recomendacao());
    }

    @Test
    void acaoJudicialDeveMandarParaAnaliseManual() {
        Pendencia acao = new Pendencia("ACAO_JUDICIAL", "Ação cível", 1, new BigDecimal("12000"));
        assertEquals(Recomendacao.ANALISE_MANUAL, politica.avaliar(relatorio(760, acao)).recomendacao());
    }
}
