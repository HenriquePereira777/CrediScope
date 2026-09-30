/**
 * Integração com a API da Serasa.
 * <p>
 * Todo o resto do sistema conversa apenas com a interface {@link br.com.crediscope.integracao.serasa.SerasaClient}.
 * Assim dá para desenvolver e testar com dados fictícios ({@code serasa.modo=mock})
 * enquanto o contrato e as credenciais da Serasa não chegam.
 */
package br.com.crediscope.integracao.serasa;
