package br.com.crediscope.auth.dto;

import java.time.Instant;

import br.com.crediscope.usuario.dto.UsuarioResponse;

/**
 * O que o frontend recebe no login e no /me.
 * O token NÃO vem aqui: ele fica só no cookie HttpOnly.
 */
public record SessaoResponse(
    UsuarioResponse usuario,
    Instant expiraEm) {
}
