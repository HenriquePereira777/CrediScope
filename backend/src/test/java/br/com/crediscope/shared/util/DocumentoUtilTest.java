package br.com.crediscope.shared.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DocumentoUtilTest {

    @Test
    void deveValidarCpf() {
        assertTrue(DocumentoUtil.isCpfValido("529.982.247-25"));
        assertFalse(DocumentoUtil.isCpfValido("111.111.111-11"));
        assertFalse(DocumentoUtil.isCpfValido("123"));
    }

    @Test
    void deveValidarCnpj() {
        assertTrue(DocumentoUtil.isCnpjValido("11.222.333/0001-81"));
        assertFalse(DocumentoUtil.isCnpjValido("11.222.333/0001-00"));
        assertFalse(DocumentoUtil.isCnpjValido("00.000.000/0000-00"));
    }
}
