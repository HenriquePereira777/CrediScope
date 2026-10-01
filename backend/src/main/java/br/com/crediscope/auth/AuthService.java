package br.com.crediscope.auth;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.crediscope.auth.dto.LoginRequest;
import br.com.crediscope.shared.exception.NaoAutorizadoException;
import br.com.crediscope.usuario.Usuario;
import br.com.crediscope.usuario.UsuarioRepository;
import br.com.crediscope.usuario.dto.UsuarioResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String MENSAGEM_ERRO = "E-mail ou senha inválidos";

    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional
    public SessaoAutenticada login(LoginRequest req) {
        Usuario usuario = usuarios.findByEmailIgnoreCase(req.email().trim())
            .orElseThrow(() -> new NaoAutorizadoException(MENSAGEM_ERRO));

        if (!usuario.isAtivo() || !passwordEncoder.matches(req.senha(), usuario.getSenhaHash())) {
            throw new NaoAutorizadoException(MENSAGEM_ERRO);
        }

        // TODO (tarefa 1.7 · 2 fatores): se usuario.isDoisFatores(), enviar código e só criar a sessão após a verificação.
        usuario.setUltimoAcesso(LocalDateTime.now());

        Instant expiraEm = Instant.now().plus(tokenService.getExpiracaoMinutos(), ChronoUnit.MINUTES);
        return new SessaoAutenticada(tokenService.gerar(usuario), expiraEm, UsuarioResponse.de(usuario));
    }
}
