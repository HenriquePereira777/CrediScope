package br.com.crediscope.shared.exception;

/** Erro de regra de negócio (ex.: limite de consultas do plano atingido). */
public class NegocioException extends RuntimeException {

    public NegocioException(String mensagem) {
        super(mensagem);
    }
}
