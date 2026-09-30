package br.com.crediscope.shared.api;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

/** Resposta paginada padrão da API (evita expor o objeto Page do Spring). */
public record Pagina<T>(
        List<T> itens,
        int pagina,
        int tamanho,
        long total,
        int totalPaginas) {

    public static <E, T> Pagina<T> de(Page<E> page, Function<E, T> conversor) {
        return new Pagina<>(
                page.getContent().stream().map(conversor).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
