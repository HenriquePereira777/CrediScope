package br.com.crediscope.cliente;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.crediscope.politica.FaixaRisco;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByDocumento(String documento);

    Page<Cliente> findByMonitoradoTrue(Pageable pageable);

    Page<Cliente> findByMonitoradoTrueAndFaixaRisco(FaixaRisco faixaRisco, Pageable pageable);

    long countByMonitoradoTrue();

    long countByMonitoradoTrueAndFaixaRisco(FaixaRisco faixaRisco);
}
