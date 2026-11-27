package br.uel.pesquisa.dao;

import br.uel.pesquisa.model.TipoQuestao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TipoQuestaoDao {

    private final JdbcTemplate jdbc;

    public TipoQuestaoDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<TipoQuestao> listar() {
        return jdbc.query("SELECT id_tipo, codigo, descricao FROM tipo_questao ORDER BY id_tipo",
                (rs, n) -> new TipoQuestao(rs.getInt("id_tipo"), rs.getString("codigo"), rs.getString("descricao")));
    }

    public Optional<TipoQuestao> buscar(int idTipo) {
        return jdbc.query("SELECT id_tipo, codigo, descricao FROM tipo_questao WHERE id_tipo = ?",
                (rs, n) -> new TipoQuestao(rs.getInt("id_tipo"), rs.getString("codigo"), rs.getString("descricao")),
                idTipo).stream().findFirst();
    }
}