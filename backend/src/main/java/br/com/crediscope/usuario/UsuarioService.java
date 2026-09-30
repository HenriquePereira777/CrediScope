package br.com.crediscope.usuario;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.crediscope.shared.exception.NegocioException;
import br.com.crediscope.shared.exception.RecursoNaoEncontradoException;
import br.com.crediscope.usuario.dto.NovoUsuarioRequest;
import br.com.crediscope.usuario.dto.UsuarioResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return repository.findAllByOrderByNomeAsc().stream().map(UsuarioResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscar(Long id) {
        return repository.findById(id)
                .map(UsuarioResponse::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }

    @Transactional
    public UsuarioResponse criar(NovoUsuarioRequest req) {
        if (repository.existsByEmailIgnoreCase(req.email())) {
            throw new NegocioException("Já existe um usuário com este e-mail");
        }
        Usuario u = new Usuario();
        u.setNome(req.nome().trim());
        u.setEmail(req.email().trim().toLowerCase());
        u.setSenhaHash(passwordEncoder.encode(req.senha()));
        u.setPerfil(req.perfil());
        u.setLimiteAprovacao(req.limiteAprovacao());
        return UsuarioResponse.de(repository.save(u));
    }
}
