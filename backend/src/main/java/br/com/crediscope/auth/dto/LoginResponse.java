package br.com.crediscope.auth.dto;

import br.com.crediscope.usuario.dto.UsuarioResponse;

public record LoginResponse(
        String token,
        long expiraEmMinutos,
        UsuarioResponse usuario) {
}
