/**
 * Autenticação: login, geração/validação do token JWT e verificação em 2 fatores.
 * <p>
 * Organização sugerida dentro do pacote:
 * <ul>
 *   <li>*Controller  – endpoints REST (/api/...)</li>
 *   <li>*Service     – regras de negócio</li>
 *   <li>*Repository  – acesso ao banco (Spring Data JPA)</li>
 *   <li>entidades    – classes @Entity</li>
 *   <li>dto          – objetos de entrada/saída da API (records)</li>
 * </ul>
 */
package br.com.crediscope.auth;
