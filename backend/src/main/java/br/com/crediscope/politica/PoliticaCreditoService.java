package br.com.crediscope.politica;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.crediscope.integracao.serasa.dto.Pendencia;
import br.com.crediscope.integracao.serasa.dto.RelatorioCredito;

/**
 * Aplica a política de crédito sobre o relatório da Serasa.
 * <p>
 * Regras atuais (fixas no código). Na fase 3 elas passam a ser lidas da tabela regra_politica,
 * editável pela tela Política de crédito:
 * <ol>
 *   <li>Score ≥ 650 → baixo risco → aprovar</li>
 *   <li>Score 450–649 → médio risco → análise manual</li>
 *   <li>Score &lt; 450 → alto risco → recusar</li>
 *   <li>Protestos somando mais de R$ 5.000 → recusar</li>
 *   <li>Ação judicial → no máximo análise manual</li>
 * </ol>
 */
@Service
public class PoliticaCreditoService {

    public static final int SCORE_APROVAR = 650;
    public static final int SCORE_ANALISE = 450;
    private static final BigDecimal LIMITE_PROTESTO = new BigDecimal("5000");
    private static final BigDecimal LIMITE_MAXIMO = new BigDecimal("50000");

    public AvaliacaoCredito avaliar(RelatorioCredito relatorio) {
        int score = relatorio.score();
        List<String> motivos = new ArrayList<>();

        FaixaRisco faixa = faixaDoScore(score);
        Recomendacao recomendacao = switch (faixa) {
            case BAIXO -> Recomendacao.APROVAR;
            case MEDIO -> Recomendacao.ANALISE_MANUAL;
            case ALTO -> Recomendacao.RECUSAR;
        };
        motivos.add("Score " + score + " na faixa de " + faixa.name().toLowerCase().replace("medio", "médio") + " risco");

        BigDecimal totalProtestos = somar(relatorio.pendencias(), "PROTESTO");
        if (totalProtestos.compareTo(LIMITE_PROTESTO) > 0) {
            recomendacao = Recomendacao.RECUSAR;
            motivos.add("Protestos acima de R$ 5.000");
        }

        boolean temAcaoJudicial = relatorio.pendencias().stream().anyMatch(p -> "ACAO_JUDICIAL".equals(p.tipo()));
        if (temAcaoJudicial && recomendacao == Recomendacao.APROVAR) {
            recomendacao = Recomendacao.ANALISE_MANUAL;
            motivos.add("Possui ação judicial: requer análise do gerente");
        }

        return new AvaliacaoCredito(faixa, recomendacao, limiteSugerido(score, recomendacao), motivos);
    }

    public static FaixaRisco faixaDoScore(int score) {
        if (score >= SCORE_APROVAR) {
            return FaixaRisco.BAIXO;
        }
        return score >= SCORE_ANALISE ? FaixaRisco.MEDIO : FaixaRisco.ALTO;
    }

    /** Limite sugerido, arredondado para baixo em múltiplos de R$ 500. */
    private BigDecimal limiteSugerido(int score, Recomendacao recomendacao) {
        long valor = switch (recomendacao) {
            case APROVAR -> (score - 600L) * 100;
            case ANALISE_MANUAL -> (score - 400L) * 20;
            case RECUSAR -> 0;
        };
        valor = Math.max(0, (valor / 500) * 500);
        return BigDecimal.valueOf(valor).min(LIMITE_MAXIMO);
    }

    private BigDecimal somar(List<Pendencia> pendencias, String tipo) {
        return pendencias.stream()
                .filter(p -> tipo.equals(p.tipo()) && p.valorTotal() != null)
                .map(Pendencia::valorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
