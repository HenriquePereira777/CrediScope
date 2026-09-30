package br.com.crediscope.painel;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.crediscope.painel.dto.ResumoPainel;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/painel")
@RequiredArgsConstructor
public class PainelController {

    private final PainelService service;

    /** GET /api/painel/resumo  →  indicadores da tela Painel */
    @GetMapping("/resumo")
    public ResumoPainel resumo() {
        return service.resumo();
    }
}
