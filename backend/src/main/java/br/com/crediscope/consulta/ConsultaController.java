package br.com.crediscope.consulta;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.crediscope.consulta.dto.ConsultaResponse;
import br.com.crediscope.consulta.dto.ConsultaResumo;
import br.com.crediscope.consulta.dto.DecisaoRequest;
import br.com.crediscope.consulta.dto.NovaConsultaRequest;
import br.com.crediscope.shared.api.Pagina;
import br.com.crediscope.shared.security.UsuarioLogado;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/consultas")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService service;

    /** POST /api/consultas  →  faz uma nova consulta */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsultaResponse consultar(@Valid @RequestBody NovaConsultaRequest req,
                                      @AuthenticationPrincipal Jwt jwt,
                                      HttpServletRequest http) {
        return service.consultar(req, UsuarioLogado.id(jwt), http.getRemoteAddr());
    }

    /** GET /api/consultas?pagina=0&tamanho=20  →  histórico */
    @GetMapping
    public Pagina<ConsultaResumo> historico(@RequestParam(defaultValue = "0") int pagina,
                                            @RequestParam(defaultValue = "20") int tamanho) {
        return service.historico(pagina, tamanho);
    }

    /** GET /api/consultas/{id}  →  resultado completo */
    @GetMapping("/{id}")
    public ConsultaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    /** PATCH /api/consultas/{id}/decisao  →  aprovar / recusar */
    @PatchMapping("/{id}/decisao")
    public ConsultaResponse decidir(@PathVariable Long id,
                                    @Valid @RequestBody DecisaoRequest req,
                                    @AuthenticationPrincipal Jwt jwt) {
        return service.decidir(id, req.decisao(), UsuarioLogado.id(jwt));
    }
}
