package br.uel.pesquisa.dao;

import br.uel.pesquisa.model.Grupo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GrupoDao {

    private final JdbcTemplate jdbc;

    public GrupoDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Grupo> listar() {
        return jdbc.query("SELECT id_grupo, nome, descricao FROM grupo ORDER BY nome",
                (rs, n) -> new Grupo(rs.getInt("id_grupo"), rs.getString("nome"), rs.getString("descricao")));
    }
}