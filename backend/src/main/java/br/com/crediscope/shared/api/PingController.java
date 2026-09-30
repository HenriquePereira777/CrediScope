package br.com.crediscope.shared.api;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint público para testar se a API está no ar: GET /api/public/ping */
@RestController
@RequestMapping("/api/public")
public class PingController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("status", "ok", "servico", "crediscope-api", "horario", OffsetDateTime.now().toString());
    }
}
