package br.com.crediscope.auth;

import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Cria, lê e apaga o cookie da sessão.
 * <ul>
 *   <li><b>HttpOnly</b>: o JavaScript da página não consegue ler o token (protege contra XSS).</li>
 *   <li><b>SameSite=Strict</b>: o navegador não envia o cookie a partir de outros sites (protege contra CSRF).</li>
 *   <li><b>Secure</b>: só trafega em HTTPS. Ligado em produção pela variável CREDISCOPE_COOKIE_SEGURO.</li>
 *   <li><b>Path=/api</b>: o cookie só é enviado para a API.</li>
 * </ul>
 */
@Component
public class SessaoCookie {

    private static final String CAMINHO = "/api";

    private final String nome;
    private final boolean seguro;

    public SessaoCookie(@Value("${app.sessao.cookie-nome}") String nome,
                        @Value("${app.sessao.cookie-seguro}") boolean seguro) {
        this.nome = nome;
        this.seguro = seguro;
    }

    /** Cookie com o token, válido até o mesmo horário em que o token expira. */
    public ResponseCookie criar(String token, Instant expiraEm) {
        Duration validade = Duration.between(Instant.now(), expiraEm);
        return base(token).maxAge(validade.isNegative() ? Duration.ZERO : validade).build();
    }

    /** Cookie vazio com validade zero: o navegador apaga o cookie da sessão. */
    public ResponseCookie apagar() {
        return base("").maxAge(Duration.ZERO).build();
    }

    /** Lê o token do cookie da requisição (ou null se não houver). */
    public String lerToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (nome.equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private ResponseCookie.ResponseCookieBuilder base(String valor) {
        return ResponseCookie.from(nome, valor)
            .httpOnly(true)
            .secure(seguro)
            .sameSite("Strict")
            .path(CAMINHO);
    }
}
