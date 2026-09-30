package br.com.crediscope.consulta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import br.com.crediscope.cliente.Cliente;
import br.com.crediscope.politica.FaixaRisco;
import br.com.crediscope.politica.Recomendacao;
import br.com.crediscope.usuario.Usuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Uma consulta de crédito. Também é o registro de auditoria exigido pela LGPD. */
@Entity
@Table(name = "consulta")
@Getter
@Setter
@NoArgsConstructor
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    /** Nulo quando a consulta foi feita automaticamente pelo sistema (monitoramento). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoConsulta tipo;

    @Column(nullable = false, length = 60)
    private String finalidade;

    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(name = "faixa_risco", length = 10)
    private FaixaRisco faixaRisco;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Recomendacao recomendacao;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Decisao decisao;

    @Column(name = "limite_sugerido", precision = 14, scale = 2)
    private BigDecimal limiteSugerido;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal custo = BigDecimal.ZERO;

    @Column(name = "ip_origem", length = 45)
    private String ipOrigem;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PendenciaConsulta> pendencias = new ArrayList<>();

    public void adicionarPendencia(PendenciaConsulta pendencia) {
        pendencia.setConsulta(this);
        pendencias.add(pendencia);
    }
}
