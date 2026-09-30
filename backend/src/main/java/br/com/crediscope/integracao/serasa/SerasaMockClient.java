package br.com.crediscope.integracao.serasa;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.com.crediscope.integracao.serasa.dto.Pendencia;
import br.com.crediscope.integracao.serasa.dto.RelatorioCredito;

/**
 * Implementação FALSA da Serasa, usada em desenvolvimento (serasa.modo=mock).
 * O mesmo documento gera sempre o mesmo resultado, o que facilita os testes.
 */
@Component
@ConditionalOnProperty(prefix = "serasa", name = "modo", havingValue = "mock", matchIfMissing = true)
public class SerasaMockClient implements SerasaClient {

    private static final List<String> EMPRESAS = List.of(
            "Comercial Sul Grãos Ltda", "Mercado Bom Preço ME", "Agropecuária Campo Verde Ltda",
            "Distribuidora Litoral Ltda", "Transportes Rápido Eireli", "Padaria Pão Quente Ltda",
            "Oficina Mecânica Três Irmãos", "Armazém Coxilha Ltda");
    private static final List<String> PESSOAS = List.of(
            "João Carlos da Silva", "Maria Aparecida Souza", "Pedro Henrique Lima",
            "Ana Paula Costa", "Lucas Martins Rocha", "Fernanda Oliveira");
    private static final List<String> CIDADES = List.of(
            "Pelotas", "Porto Alegre", "Rio Grande", "Canguçu", "Santa Maria", "Bagé");

    @Override
    public RelatorioCredito consultarCnpj(String cnpj) {
        return gerar(cnpj, EMPRESAS);
    }

    @Override
    public RelatorioCredito consultarCpf(String cpf) {
        return gerar(cpf, PESSOAS);
    }

    private RelatorioCredito gerar(String documento, List<String> nomes) {
        int semente = Math.floorMod(documento.hashCode(), 10_000);
        int score = 300 + semente % 700;

        List<Pendencia> pendencias = new ArrayList<>();
        if (score < 450) {
            pendencias.add(new Pendencia("PROTESTO", "Protesto em cartório", 2, new BigDecimal("6200.00")));
            pendencias.add(new Pendencia("RESTRICAO", "Dívida com instituição financeira", 1, new BigDecimal("3100.00")));
        } else if (score < 600) {
            pendencias.add(new Pendencia("RESTRICAO", "Dívida com empresa de telefonia", 1, new BigDecimal("850.00")));
        } else if (score < 720 && semente % 3 == 0) {
            pendencias.add(new Pendencia("ACAO_JUDICIAL", "Ação cível", 1, new BigDecimal("12000.00")));
        }

        return new RelatorioCredito(
                documento,
                nomes.get(semente % nomes.size()),
                CIDADES.get(semente % CIDADES.size()),
                "RS",
                score,
                pendencias,
                OffsetDateTime.now());
    }
}
