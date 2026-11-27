package br.uel.pesquisa.dao;

import br.uel.pesquisa.model.Convidado;
import br.uel.pesquisa.model.MinhaPesquisa;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//Tabela CONVITE quem deve responder cada pesquisa e quem já respondeu
@Repository
public class ConviteDao {

    private final JdbcTemplate jdbc;

    public ConviteDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Convidado> listar(int idPesquisa) {
        return jdbc.query("""
                SELECT u.id_usuario, u.nome, u.email, g.nome AS grupo, c.dt_convite, c.dt_resp
                FROM convite c
                JOIN usuario u    ON u.id_usuario = c.id_usuario
                LEFT JOIN grupo g ON g.id_grupo   = u.id_grupo
                WHERE c.id_pesquisa = ?
                ORDER BY g.nome, u.nome
                """, (rs, n) -> new Convidado(rs.getInt("id_usuario"), rs.getString("nome"), rs.getString("email"),
                rs.getString("grupo"), rs.getObject("dt_convite", LocalDateTime.class),
                rs.getObject("dt_resp", LocalDateTime.class)), idPesquisa);
    }

    //Convida um respondente e ignora se já estava convidado
    public int convidarUsuario(int idPesquisa, int idUsuario) {
        return jdbc.update("""
                INSERT INTO convite (id_pesquisa, id_usuario)
                SELECT ?, id_usuario FROM usuario WHERE id_usuario = ? AND papel = 'RESPONDENTE'
                ON CONFLICT (id_pesquisa, id_usuario) DO NOTHING
                """, idPesquisa, idUsuario);
    }

    //Convida todos os respondentes de um grupo e ignora quem já estava convidado
    public int convidarGrupo(int idPesquisa, int idGrupo) {
        return jdbc.update("""
                INSERT INTO convite (id_pesquisa, id_usuario)
                SELECT ?, id_usuario FROM usuario WHERE id_grupo = ? AND papel = 'RESPONDENTE'
                ON CONFLICT (id_pesquisa, id_usuario) DO NOTHING
                """, idPesquisa, idGrupo);
    }

    //Retira um convite que ainda não foi respondido
    public int remover(int idPesquisa, int idUsuario) {
        return jdbc.update("DELETE FROM convite WHERE id_pesquisa = ? AND id_usuario = ? AND dt_resp IS NULL",
                idPesquisa, idUsuario);
    }


    // --- Lado do usuário

    private static final String SELECT_MINHAS = """
            SELECT p.id_pesquisa, p.titulo, p.descricao, p.inicio, p.fim, p.anonima,
                   (SELECT COUNT(*) FROM pesquisa_questao pq WHERE pq.id_pesquisa = p.id_pesquisa) AS qtd_questoes,
                   c.dt_convite, c.dt_resp
            FROM convite c
            JOIN pesquisa p ON p.id_pesquisa = c.id_pesquisa
            WHERE c.id_usuario = ?
            """;

    private static MinhaPesquisa mapearMinha(ResultSet rs, int n) throws SQLException {
        return new MinhaPesquisa(rs.getInt("id_pesquisa"), rs.getString("titulo"), rs.getString("descricao"),
                rs.getObject("inicio", LocalDateTime.class), rs.getObject("fim", LocalDateTime.class),
                rs.getBoolean("anonima"), rs.getInt("qtd_questoes"),
                rs.getObject("dt_convite", LocalDateTime.class), rs.getObject("dt_resp", LocalDateTime.class));
    }

    //pesquisas que o usuário foi convidado
    public List<MinhaPesquisa> listarDoUsuario(int idUsuario) {
        return jdbc.query(SELECT_MINHAS + " ORDER BY p.fim DESC", ConviteDao::mapearMinha, idUsuario);
    }

    public Optional<MinhaPesquisa> buscarDoUsuario(int idUsuario, int idPesquisa) {
        return jdbc.query(SELECT_MINHAS + " AND c.id_pesquisa = ?", ConviteDao::mapearMinha, idUsuario, idPesquisa)
                .stream().findFirst();
    }

    public void marcarRespondido(int idPesquisa, int idUsuario) {
        jdbc.update("UPDATE convite SET dt_resp = CURRENT_TIMESTAMP WHERE id_pesquisa = ? AND id_usuario = ?",
                idPesquisa, idUsuario);
    }
}