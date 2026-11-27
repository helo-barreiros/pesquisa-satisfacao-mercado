package br.uel.pesquisa.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

//Consultas dos relatórios (relatorios.sql).
//Cada linha volta como um Map com nome da coluna -> valor.
@Repository
public class RelatorioDao {

    private final JdbcTemplate jdbc;

    public RelatorioDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    //Relatório 1
    public List<Map<String, Object>> mediaPorTema(LocalDate inicio, LocalDate fim, List<Integer> grupos) {
        // um "?" para cada grupo escolhido: IN (?, ?, ?)
        String marcadores = String.join(", ", Collections.nCopies(grupos.size(), "?"));
        String sql = """
                SELECT t.nome                                   AS tema,
                       ROUND(AVG(r.valor_escala), 2)            AS media,
                       COUNT(*)                                 AS respostas,
                       ROUND(100.0 * SUM(CASE WHEN r.valor_escala >= 4 THEN 1 ELSE 0 END) / COUNT(*), 1)
                                                                AS pct_satisfeitos
                FROM resposta  r
                JOIN questao   q ON q.id_questao = r.id_questao
                JOIN tema      t ON t.id_tema    = q.id_tema
                JOIN submissao s ON s.id_sub     = r.id_sub
                WHERE r.valor_escala IS NOT NULL
                  AND s.dt_envio BETWEEN ? AND ?
                  AND s.id_grupo IN (%s)
                GROUP BY t.id_tema, t.nome
                HAVING COUNT(*) >= 5
                ORDER BY media DESC
                """.formatted(marcadores);

        List<Object> parametros = new ArrayList<>();
        parametros.add(inicio);
        parametros.add(fim);
        parametros.addAll(grupos);
        return jdbc.queryForList(sql, parametros.toArray());
    }

    //Relatório 2
    public List<Map<String, Object>> rankingPiores() {
        return jdbc.queryForList("""
                SELECT RANK() OVER (ORDER BY AVG(r.valor_escala))  AS posicao,
                       q.enunciado,
                       t.nome                                      AS tema,
                       ROUND(AVG(r.valor_escala), 2)               AS media,
                       ROUND(100.0 * SUM(CASE WHEN r.valor_escala <= 2 THEN 1 ELSE 0 END) / COUNT(*), 1)
                                                                   AS pct_insatisfeitos,
                       COUNT(*)                                    AS respostas
                FROM resposta r
                JOIN questao  q ON q.id_questao = r.id_questao
                JOIN tema     t ON t.id_tema    = q.id_tema
                WHERE r.valor_escala IS NOT NULL
                GROUP BY q.id_questao, q.enunciado, t.nome
                HAVING AVG(r.valor_escala) < (SELECT AVG(r2.valor_escala)
                                              FROM resposta r2
                                              WHERE r2.valor_escala IS NOT NULL)
                ORDER BY posicao, q.id_questao
                FETCH FIRST 10 ROWS ONLY
                """);
    }

    //Relatório 3
    public List<Map<String, Object>> participacao(String busca) {
        return jdbc.queryForList("""
                SELECT p.titulo,
                       to_char(p.inicio, 'DD/MM/YYYY')         AS inicio,
                       to_char(p.fim, 'DD/MM/YYYY')            AS fim,
                       COUNT(c.id_usuario)                     AS convidados,
                       COUNT(c.dt_resp)                        AS responderam,
                       COUNT(c.id_usuario) - COUNT(c.dt_resp)  AS pendentes,
                       ROUND(100.0 * COUNT(c.dt_resp) / NULLIF(COUNT(c.id_usuario), 0), 1) AS taxa
                FROM pesquisa p
                LEFT JOIN convite c ON c.id_pesquisa = p.id_pesquisa
                WHERE LOWER(p.titulo) LIKE LOWER(?)
                GROUP BY p.id_pesquisa, p.titulo, p.inicio, p.fim
                ORDER BY p.inicio
                """, "%" + busca + "%");
    }
}
