package br.com.crediscope.auth;

import java.time.Instant;

import br.com.crediscope.usuario.dto.UsuarioResponse;

/**
 * Resultado interno do login: o token (vai para o cookie, nunca para o corpo da resposta),
 * o horário de expiração e os dados do usuário.
 */
public record SessaoAutenticada(
    String token,
    Instant expiraEm,
    UsuarioResponse usuario) {
}
