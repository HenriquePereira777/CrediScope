package br.com.crediscope.consulta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.crediscope.politica.Recomendacao;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    @Query("""
            select c from Consulta c
              join fetch c.cliente
              left join fetch c.usuario
             where c.id = :id
            """)
    Optional<Consulta> buscarCompleta(@Param("id") Long id);

    @Query(value = """
            select c from Consulta c
              join fetch c.cliente
              left join fetch c.usuario
             order by c.criadoEm desc
            """,
            countQuery = "select count(c) from Consulta c")
    Page<Consulta> listarHistorico(Pageable pageable);

    long countByCriadoEmGreaterThanEqual(LocalDateTime inicio);

    long countByCriadoEmGreaterThanEqualAndRecomendacao(LocalDateTime inicio, Recomendacao recomendacao);

    List<Consulta> findByCriadoEmGreaterThanEqual(LocalDateTime inicio);
}
