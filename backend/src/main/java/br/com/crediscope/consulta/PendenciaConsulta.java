package br.com.crediscope.consulta;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pendencia")
@Getter
@Setter
@NoArgsConstructor
public class PendenciaConsulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;

    @Column(nullable = false, length = 30)
    private String tipo;

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false)
    private int quantidade;

    @Column(name = "valor_total", precision = 14, scale = 2)
    private BigDecimal valorTotal;

    public PendenciaConsulta(String tipo, String descricao, int quantidade, BigDecimal valorTotal) {
        this.tipo = tipo;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
    }
}
