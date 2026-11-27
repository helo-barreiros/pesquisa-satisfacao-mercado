package br.uel.pesquisa.dao;

import br.uel.pesquisa.model.Alternativa;
import br.uel.pesquisa.model.Questao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class QuestaoDao {

    private static final String SELECT = """
            SELECT q.id_questao, q.enunciado, q.ativa, q.data_criacao,
                   tq.id_tipo, tq.codigo AS tipo, t.id_tema, t.nome AS tema,
                   (SELECT COUNT(*) FROM pesquisa_questao pq WHERE pq.id_questao = q.id_questao) AS usos
            FROM questao q
            JOIN tipo_questao tq ON tq.id_tipo = q.id_tipo
            JOIN tema t          ON t.id_tema  = q.id_tema
            """;

    private final JdbcTemplate jdbc;

    public QuestaoDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static Questao mapear(ResultSet rs, int linha) throws SQLException {
        return new Questao(rs.getInt("id_questao"), rs.getString("enunciado"), rs.getBoolean("ativa"),
                rs.getObject("data_criacao", LocalDateTime.class),
                rs.getInt("id_tipo"), rs.getString("tipo"), rs.getInt("id_tema"), rs.getString("tema"),
                rs.getInt("usos"), List.of());
    }

    //Lista as questões
    public List<Questao> listar(Integer idTema, String busca) {
        StringBuilder sql = new StringBuilder(SELECT).append(" WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        if (idTema != null) {
            sql.append(" AND q.id_tema = ?");
            params.add(idTema);
        }
        if (busca != null && !busca.isBlank()) {
            sql.append(" AND LOWER(q.enunciado) LIKE LOWER(?)");
            params.add("%" + busca.trim() + "%");
        }
        sql.append(" ORDER BY t.nome, q.id_questao");

        List<Questao> questoes = jdbc.query(sql.toString(), QuestaoDao::mapear, params.toArray());
        Map<Integer, List<Alternativa>> alternativas = alternativasPorQuestao();
        return questoes.stream()
                .map(q -> q.comAlternativas(alternativas.getOrDefault(q.id(), List.of())))
                .toList();
    }

    //Todas as alternativas agrupadas pelo id_questao
    private Map<Integer, List<Alternativa>> alternativasPorQuestao() {
        Map<Integer, List<Alternativa>> mapa = new HashMap<>();
        jdbc.query("SELECT id_alternativa, id_questao, ordem, texto FROM alternativa ORDER BY id_questao, ordem",
                rs -> {
                    mapa.computeIfAbsent(rs.getInt("id_questao"), k -> new ArrayList<>())
                            .add(new Alternativa(rs.getInt("id_alternativa"), rs.getInt("ordem"), rs.getString("texto")));
                });
        return mapa;
    }
}