package br.com.crediscope.consulta;

import java.time.LocalDateTime;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.crediscope.cliente.Cliente;
import br.com.crediscope.cliente.ClienteRepository;
import br.com.crediscope.consulta.dto.ConsultaResponse;
import br.com.crediscope.consulta.dto.ConsultaResumo;
import br.com.crediscope.consulta.dto.NovaConsultaRequest;
import br.com.crediscope.integracao.serasa.SerasaClient;
import br.com.crediscope.integracao.serasa.dto.RelatorioCredito;
import br.com.crediscope.politica.AvaliacaoCredito;
import br.com.crediscope.politica.PoliticaCreditoService;
import br.com.crediscope.shared.api.Pagina;
import br.com.crediscope.shared.exception.NegocioException;
import br.com.crediscope.shared.exception.RecursoNaoEncontradoException;
import br.com.crediscope.shared.util.DocumentoUtil;
import br.com.crediscope.usuario.Usuario;
import br.com.crediscope.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final SerasaClient serasa;
    private final PoliticaCreditoService politica;
    private final ConsultaRepository consultas;
    private final ClienteRepository clientes;
    private final UsuarioRepository usuarios;

    /**
     * Faz uma nova consulta:
     * valida o documento → consulta a Serasa → aplica a política → atualiza o cliente → grava o histórico.
     */
    @Transactional
    public ConsultaResponse consultar(NovaConsultaRequest req, Long usuarioId, String ip) {
        String documento = DocumentoUtil.somenteNumeros(req.documento());
        String tipoPessoa;
        RelatorioCredito relatorio;

        if (documento.length() == 11) {
            if (!DocumentoUtil.isCpfValido(documento)) {
                throw new NegocioException("CPF inválido");
            }
            tipoPessoa = "PF";
            relatorio = serasa.consultarCpf(documento);
        } else if (documento.length() == 14) {
            if (!DocumentoUtil.isCnpjValido(documento)) {
                throw new NegocioException("CNPJ inválido");
            }
            tipoPessoa = "PJ";
            relatorio = serasa.consultarCnpj(documento);
        } else {
            throw new NegocioException("Informe um CPF (11 dígitos) ou um CNPJ (14 dígitos)");
        }

        AvaliacaoCredito avaliacao = politica.avaliar(relatorio);

        Cliente cliente = clientes.findByDocumento(documento).orElseGet(Cliente::new);
        cliente.setDocumento(documento);
        cliente.setTipoPessoa(tipoPessoa);
        cliente.setNome(relatorio.nome());
        cliente.setCidade(relatorio.cidade());
        cliente.setUf(relatorio.uf());
        cliente.setScoreAtual(relatorio.score());
        cliente.setFaixaRisco(avaliacao.faixaRisco());
        cliente.setAtualizadoScoreEm(LocalDateTime.now());
        if (req.monitorar()) {
            cliente.setMonitorado(true);
        }
        clientes.save(cliente);

        Consulta consulta = new Consulta();
        consulta.setCliente(cliente);
        consulta.setUsuario(usuarioId == null ? null : usuarios.getReferenceById(usuarioId));
        consulta.setTipo(req.tipoOuPadrao());
        consulta.setFinalidade(req.finalidade());
        consulta.setScore(relatorio.score());
        consulta.setFaixaRisco(avaliacao.faixaRisco());
        consulta.setRecomendacao(avaliacao.recomendacao());
        consulta.setLimiteSugerido(avaliacao.limiteSugerido());
        consulta.setCusto(req.tipoOuPadrao().getCusto());
        consulta.setIpOrigem(ip);
        relatorio.pendencias().forEach(p -> consulta.adicionarPendencia(
                new PendenciaConsulta(p.tipo(), p.descricao(), p.quantidade(), p.valorTotal())));
        consultas.save(consulta);

        return ConsultaResponse.de(consulta);
    }

    @Transactional(readOnly = true)
    public ConsultaResponse buscar(Long id) {
        return ConsultaResponse.de(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Pagina<ConsultaResumo> historico(int pagina, int tamanho) {
        PageRequest pageable = PageRequest.of(Math.max(0, pagina), Math.min(100, Math.max(1, tamanho)));
        return Pagina.de(consultas.listarHistorico(pageable), ConsultaResumo::de);
    }

    /** Registra a decisão do analista (aprovar / recusar / deixar em análise). */
    @Transactional
    public ConsultaResponse decidir(Long id, Decisao decisao, Long usuarioId) {
        Consulta consulta = buscarEntidade(id);

        if (decisao == Decisao.APROVADO) {
            Usuario usuario = usuarios.findById(usuarioId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
            if (usuario.getLimiteAprovacao() != null && consulta.getLimiteSugerido() != null
                    && consulta.getLimiteSugerido().compareTo(usuario.getLimiteAprovacao()) > 0) {
                throw new NegocioException("Valor acima do seu limite de aprovação. Encaminhe para o gerente.");
            }
            consulta.getCliente().setLimiteConcedido(consulta.getLimiteSugerido());
        }

        consulta.setDecisao(decisao);
        return ConsultaResponse.de(consulta);
    }

    private Consulta buscarEntidade(Long id) {
        return consultas.buscarCompleta(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada"));
    }
}
