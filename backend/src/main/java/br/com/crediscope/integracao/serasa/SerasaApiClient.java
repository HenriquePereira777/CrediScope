package br.com.crediscope.integracao.serasa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.com.crediscope.integracao.serasa.dto.RelatorioCredito;

/**
 * Integração REAL com a Serasa (serasa.modo=api).
 * <p>
 * Será implementada quando o contrato for assinado e a Serasa enviar a documentação
 * e as credenciais (client-id / client-secret). Use {@link SerasaProperties} para ler a configuração.
 */
@Component
@ConditionalOnProperty(prefix = "serasa", name = "modo", havingValue = "api")
public class SerasaApiClient implements SerasaClient {

    private final SerasaProperties properties;

    public SerasaApiClient(SerasaProperties properties) {
        this.properties = properties;
    }

    @Override
    public RelatorioCredito consultarCnpj(String cnpj) {
        throw new UnsupportedOperationException("Integração real com a Serasa ainda não implementada (" + properties.baseUrl() + ")");
    }

    @Override
    public RelatorioCredito consultarCpf(String cpf) {
        throw new UnsupportedOperationException("Integração real com a Serasa ainda não implementada (" + properties.baseUrl() + ")");
    }
}
