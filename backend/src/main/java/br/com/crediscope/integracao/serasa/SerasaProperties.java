package br.com.crediscope.integracao.serasa;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Configurações lidas do application.yml (bloco "serasa"). */
@ConfigurationProperties(prefix = "serasa")
public record SerasaProperties(
        String modo,
        String baseUrl,
        String clientId,
        String clientSecret,
        int timeoutSegundos) {
}
