package br.com.crediscope.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.crediscope.auth.dto.LoginRequest;
import br.com.crediscope.auth.dto.SessaoResponse;
import br.com.crediscope.shared.security.UsuarioLogado;
import br.com.crediscope.usuario.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final SessaoCookie sessaoCookie;

    /**
     * POST /api/auth/login
     * Confere e-mail e senha, grava o token no cookie HttpOnly e devolve { usuario, expiraEm }.
     */
    @PostMapping("/login")
    public ResponseEntity<SessaoResponse> login(@Valid @RequestBody LoginRequest req) {
        SessaoAutenticada sessao = authService.login(req);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, sessaoCookie.criar(sessao.token(), sessao.expiraEm()).toString())
            .body(new SessaoResponse(sessao.usuario(), sessao.expiraEm()));
    }

    /** POST /api/auth/logout  →  apaga o cookie da sessão. */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, sessaoCookie.apagar().toString())
            .build();
    }

    /** GET /api/auth/me  →  quem está logado e até quando a sessão vale. */
    @GetMapping("/me")
    public SessaoResponse me(@AuthenticationPrincipal Jwt jwt) {
        return new SessaoResponse(usuarioService.buscar(UsuarioLogado.id(jwt)), jwt.getExpiresAt());
    }
}
