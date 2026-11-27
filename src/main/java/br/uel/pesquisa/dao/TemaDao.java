package br.uel.pesquisa.dao;

import br.uel.pesquisa.model.Tema;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TemaDao {

    private final JdbcTemplate jdbc;

    public TemaDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Tema> listar() {
        return jdbc.query("SELECT id_tema, nome, descricao FROM tema ORDER BY nome",
                (rs, n) -> new Tema(rs.getInt("id_tema"), rs.getString("nome"), rs.getString("descricao")));
    }

    public boolean existe(int idTema) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM tema WHERE id_tema = ?", Integer.class, idTema);
        return n != null && n > 0;
    }
}
