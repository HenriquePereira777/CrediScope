package br.com.crediscope.integracao.serasa;

import br.com.crediscope.integracao.serasa.dto.RelatorioCredito;

/** Contrato da integração com a Serasa. */
public interface SerasaClient {

    /** Consulta de pessoa jurídica pelo CNPJ (somente números). */
    RelatorioCredito consultarCnpj(String cnpj);

    /** Consulta de pessoa física pelo CPF (somente números). */
    RelatorioCredito consultarCpf(String cpf);
}
