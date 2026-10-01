package br.com.crediscope.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.com.crediscope.auth.SessaoCookie;

/**
 * Segurança da API.
 * <ul>
 *   <li>O token JWT viaja no cookie HttpOnly da sessão (ver {@link SessaoCookie}), não no JavaScript.</li>
 *   <li>O token é validado pelo Spring Security (Resource Server / JWT).</li>
 *   <li>O claim "perfil" do token vira a role (ex.: ROLE_ADMINISTRADOR), usada no @PreAuthorize.</li>
 *   <li>CSRF: coberto pelo cookie SameSite=Strict + frontend e API no mesmo endereço.</li>
 * </ul>
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /** Rotas abertas: não exigem login e ignoram o cookie (mesmo que esteja vencido). */
    private static final String[] ROTAS_PUBLICAS = {
        "/api/public/", "/api/auth/login", "/api/auth/logout", "/actuator/health"
    };

    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, SessaoCookie sessaoCookie) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**", "/api/auth/login", "/api/auth/logout", "/actuator/health").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth
                .bearerTokenResolver(lerTokenDoCookie(sessaoCookie))
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    /**
     * Diz ao Spring Security onde está o token: no cookie da sessão.
     * Nas rotas públicas devolve null, para um cookie vencido não bloquear o login.
     */
    private BearerTokenResolver lerTokenDoCookie(SessaoCookie sessaoCookie) {
        return request -> {
            String caminho = request.getRequestURI();
            for (String publica : ROTAS_PUBLICAS) {
                if (caminho.startsWith(publica)) {
                    return null;
                }
            }
            return sessaoCookie.lerToken(request);
        };
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter perfis = new JwtGrantedAuthoritiesConverter();
        perfis.setAuthoritiesClaimName("perfil");
        perfis.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(perfis);
        return converter;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
