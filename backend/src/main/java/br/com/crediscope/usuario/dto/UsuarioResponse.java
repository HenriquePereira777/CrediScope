package br.com.crediscope.usuario.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.crediscope.usuario.Perfil;
import br.com.crediscope.usuario.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Perfil perfil,
        BigDecimal limiteAprovacao,
        boolean doisFatores,
        boolean ativo,
        LocalDateTime ultimoAcesso) {

    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil(),
                u.getLimiteAprovacao(), u.isDoisFatores(), u.isAtivo(), u.getUltimoAcesso());
    }
}
