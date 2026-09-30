package br.com.crediscope.shared.exception;

/** Login inválido ou usuário sem acesso. Vira HTTP 401. */
public class NaoAutorizadoException extends RuntimeException {

    public NaoAutorizadoException(String mensagem) {
        super(mensagem);
    }
}
