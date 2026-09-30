/**
 * Alertas do monitoramento: nova restrição, queda de score etc.
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
package br.com.crediscope.alerta;
