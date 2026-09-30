package br.com.crediscope.shared.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import br.com.crediscope.shared.exception.NaoAutorizadoException;
import br.com.crediscope.shared.exception.NegocioException;
import br.com.crediscope.shared.exception.RecursoNaoEncontradoException;

/** Converte exceções em respostas JSON padronizadas ({@link ApiErro}). */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErro> validacao(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();
        return resposta(HttpStatus.BAD_REQUEST, "Dados inválidos", detalhes);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiErro> requisicaoInvalida(Exception ex) {
        return resposta(HttpStatus.BAD_REQUEST, "Requisição inválida", List.of());
    }

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<ApiErro> negocio(NegocioException ex) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), List.of());
    }

    @ExceptionHandler(NaoAutorizadoException.class)
    public ResponseEntity<ApiErro> naoAutorizado(NaoAutorizadoException ex) {
        return resposta(HttpStatus.UNAUTHORIZED, ex.getMessage(), List.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErro> acessoNegado(AccessDeniedException ex) {
        return resposta(HttpStatus.FORBIDDEN, "Você não tem permissão para esta ação", List.of());
    }

    @ExceptionHandler({RecursoNaoEncontradoException.class, NoResourceFoundException.class})
    public ResponseEntity<ApiErro> naoEncontrado(Exception ex) {
        String msg = ex instanceof RecursoNaoEncontradoException ? ex.getMessage() : "Recurso não encontrado";
        return resposta(HttpStatus.NOT_FOUND, msg, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErro> inesperado(Exception ex) {
        log.error("Erro inesperado", ex);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno. Tente novamente em instantes.", List.of());
    }

    private ResponseEntity<ApiErro> resposta(HttpStatus status, String mensagem, List<String> detalhes) {
        return ResponseEntity.status(status).body(ApiErro.de(status.value(), mensagem, detalhes));
    }
}
