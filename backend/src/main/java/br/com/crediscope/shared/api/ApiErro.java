package br.com.crediscope.shared.api;

import java.time.OffsetDateTime;
import java.util.List;

/** Formato padrão de erro devolvido pela API para o frontend. */
public record ApiErro(
        int status,
        String mensagem,
        List<String> detalhes,
        OffsetDateTime horario) {

    public static ApiErro de(int status, String mensagem) {
        return new ApiErro(status, mensagem, List.of(), OffsetDateTime.now());
    }

    public static ApiErro de(int status, String mensagem, List<String> detalhes) {
        return new ApiErro(status, mensagem, detalhes, OffsetDateTime.now());
    }
}
