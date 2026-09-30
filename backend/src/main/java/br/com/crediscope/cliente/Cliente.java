package br.com.crediscope.cliente;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.crediscope.politica.FaixaRisco;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** "PF" ou "PJ" */
    @Column(name = "tipo_pessoa", nullable = false, length = 2)
    private String tipoPessoa;

    /** CPF ou CNPJ, somente números */
    @Column(nullable = false, unique = true, length = 14)
    private String documento;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String uf;

    @Column(name = "score_atual")
    private Integer scoreAtual;

    @Enumerated(EnumType.STRING)
    @Column(name = "faixa_risco", length = 10)
    private FaixaRisco faixaRisco;

    @Column(name = "limite_concedido", precision = 14, scale = 2)
    private BigDecimal limiteConcedido;

    @Column(name = "valor_em_aberto", precision = 14, scale = 2)
    private BigDecimal valorEmAberto;

    @Column(nullable = false)
    private boolean monitorado;

    @Column(name = "codigo_erp", length = 50)
    private String codigoErp;

    @Column(name = "atualizado_score_em")
    private LocalDateTime atualizadoScoreEm;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;
}
