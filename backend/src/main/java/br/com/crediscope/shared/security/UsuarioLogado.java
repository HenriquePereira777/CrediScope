package br.com.crediscope.shared.security;

import org.springframework.security.oauth2.jwt.Jwt;

/** Lê os dados do usuário logado a partir do token JWT. */
public final class UsuarioLogado {

    private UsuarioLogado() {
    }

    public static Long id(Jwt jwt) {
        Object valor = jwt == null ? null : jwt.getClaim("id");
        return valor instanceof Number numero ? numero.longValue() : null;
    }
}
