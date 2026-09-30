package br.com.crediscope.cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.crediscope.cliente.dto.ClienteResponse;
import br.com.crediscope.politica.FaixaRisco;
import br.com.crediscope.shared.api.Pagina;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteRepository repository;

    /** GET /api/clientes?faixa=BAIXO&pagina=0&tamanho=20  →  carteira (clientes monitorados) */
    @GetMapping
    @Transactional(readOnly = true)
    public Pagina<ClienteResponse> carteira(
            @RequestParam(required = false) FaixaRisco faixa,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        PageRequest pageable = PageRequest.of(Math.max(0, pagina), Math.min(100, Math.max(1, tamanho)),
                Sort.by(Sort.Direction.DESC, "scoreAtual"));
        Page<Cliente> page = faixa == null
                ? repository.findByMonitoradoTrue(pageable)
                : repository.findByMonitoradoTrueAndFaixaRisco(faixa, pageable);
        return Pagina.de(page, ClienteResponse::de);
    }
}
