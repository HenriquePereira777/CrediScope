package br.com.crediscope.shared.util;

/** Utilitários para CPF e CNPJ (limpeza e validação dos dígitos verificadores). */
public final class DocumentoUtil {

    private DocumentoUtil() {
    }

    /** Remove tudo que não for número. */
    public static String somenteNumeros(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    public static boolean isCpfValido(String valor) {
        String cpf = somenteNumeros(valor);
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            return false;
        }
        int d1 = digitoCpf(cpf, 9);
        int d2 = digitoCpf(cpf, 10);
        return d1 == cpf.charAt(9) - '0' && d2 == cpf.charAt(10) - '0';
    }

    public static boolean isCnpjValido(String valor) {
        String cnpj = somenteNumeros(valor);
        if (cnpj.length() != 14 || cnpj.chars().distinct().count() == 1) {
            return false;
        }
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int d1 = digitoCnpj(cnpj, pesos1);
        int d2 = digitoCnpj(cnpj, pesos2);
        return d1 == cnpj.charAt(12) - '0' && d2 == cnpj.charAt(13) - '0';
    }

    private static int digitoCpf(String cpf, int tamanho) {
        int soma = 0;
        for (int i = 0; i < tamanho; i++) {
            soma += (cpf.charAt(i) - '0') * (tamanho + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    private static int digitoCnpj(String cnpj, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += (cnpj.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
