package br.com.crediscope.usuario.dto;

import java.math.BigDecimal;

import br.com.crediscope.usuario.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record NovoUsuarioRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(min = 8, message = "deve ter pelo menos 8 caracteres") String senha,
        @NotNull Perfil perfil,
        @PositiveOrZero BigDecimal limiteAprovacao) {
}
