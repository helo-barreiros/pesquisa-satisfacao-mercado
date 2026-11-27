package br.uel.pesquisa.dao;

import br.uel.pesquisa.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioDao {

    private static final String SELECT = """
            SELECT u.id_Usuario, u.nome, u.email, u.papel, u.id_grupo, g.nome AS grupo
            FROM Usuario u
            LEFT JOIN grupo g ON g.id_grupo = u.id_grupo
            """;

    private final JdbcTemplate jdbc;

    public UsuarioDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static Usuario mapear(ResultSet rs, int linha) throws SQLException {
        return new Usuario(rs.getInt("id_usuario"), rs.getString("nome"), rs.getString("email"),
                rs.getString("papel"), (Integer) rs.getObject("id_grupo"), rs.getString("grupo"));
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return jdbc.query(SELECT + " WHERE LOWER(u.email) = LOWER(?)", UsuarioDao::mapear, email.trim())
                .stream().findFirst();
    }

    public List<Usuario> listarRespondentes() {
        return jdbc.query(SELECT + " WHERE u.papel = 'RESPONDENTE' ORDER BY g.nome, u.nome", UsuarioDao::mapear);
    }
}
