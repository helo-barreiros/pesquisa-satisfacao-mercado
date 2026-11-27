package br.uel.pesquisa.dao;

import br.uel.pesquisa.model.Pesquisa;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class PesquisaDao {

    private static final String SELECT = """
            SELECT p.id_pesquisa, p.titulo, p.descricao, p.inicio, p.fim, p.anonima,
                   (SELECT COUNT(*) FROM pesquisa_questao pq WHERE pq.id_pesquisa = p.id_pesquisa) AS qtd_questoes,
                   (SELECT COUNT(*) FROM convite c WHERE c.id_pesquisa = p.id_pesquisa) AS convidados,
                   (SELECT COUNT(*) FROM convite c WHERE c.id_pesquisa = p.id_pesquisa AND c.dt_resp IS NOT NULL) AS responderam
            FROM pesquisa p
            """;

    private final JdbcTemplate jdbc;

    public PesquisaDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static Pesquisa mapear(ResultSet rs, int linha) throws SQLException {
        return new Pesquisa(rs.getInt("id_pesquisa"), rs.getString("titulo"), rs.getString("descricao"),
                rs.getObject("inicio", LocalDateTime.class), rs.getObject("fim", LocalDateTime.class),
                rs.getBoolean("anonima"), rs.getInt("qtd_questoes"), rs.getInt("convidados"),
                rs.getInt("responderam"));
    }

    public List<Pesquisa> listar() {
        return jdbc.query(SELECT + " ORDER BY p.inicio DESC", PesquisaDao::mapear);
    }

    public Optional<Pesquisa> buscar(int idPesquisa) {
        return jdbc.query(SELECT + " WHERE p.id_pesquisa = ?", PesquisaDao::mapear, idPesquisa)
                .stream().findFirst();
    }

    public int inserir(String titulo, String descricao, LocalDateTime inicio, LocalDateTime fim,
                       boolean anonima, int criadaPor) {
        return jdbc.queryForObject("""
                INSERT INTO pesquisa (titulo, descricao, inicio, fim, anonima, criada_por)
                VALUES (?, ?, ?, ?, ?, ?) RETURNING id_pesquisa
                """, Integer.class, titulo, descricao, inicio, fim, anonima, criadaPor);
    }
}