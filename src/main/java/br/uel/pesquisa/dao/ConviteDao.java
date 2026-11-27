package br.uel.pesquisa.dao;

import br.uel.pesquisa.model.Convidado;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
}