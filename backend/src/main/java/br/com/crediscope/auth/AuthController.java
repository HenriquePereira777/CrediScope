package br.com.crediscope.auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.crediscope.auth.dto.LoginRequest;
import br.com.crediscope.auth.dto.LoginResponse;
import br.com.crediscope.shared.security.UsuarioLogado;
import br.com.crediscope.usuario.UsuarioService;
import br.com.crediscope.usuario.dto.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    /** POST /api/auth/login  →  { token, expiraEmMinutos, usuario } */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    /** GET /api/auth/me  →  dados do usuário logado */
    @GetMapping("/me")
    public UsuarioResponse me(@AuthenticationPrincipal Jwt jwt) {
        return usuarioService.buscar(UsuarioLogado.id(jwt));
    }
}
