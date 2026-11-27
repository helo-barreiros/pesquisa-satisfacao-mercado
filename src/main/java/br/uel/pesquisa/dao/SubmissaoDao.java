package br.uel.pesquisa.dao;

import br.uel.pesquisa.dto.RespostaEnviada;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

//Tabelas resposta
@Repository
public class SubmissaoDao {

    private final JdbcTemplate jdbc;

    public SubmissaoDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    //Cria o envio e devolve o id (UUID) gerado pelo banco. idUsuario é null nas pesquisas anônimas
    public UUID inserir(int idPesquisa, Integer idUsuario, int idGrupo) {
        return jdbc.queryForObject("""
                INSERT INTO submissao (id_pesquisa, id_usuario, id_grupo)
                VALUES (?, ?, ?)
                RETURNING id_sub
                """, UUID.class, idPesquisa, idUsuario, idGrupo);
    }

    public void inserirResposta(UUID idSub, RespostaEnviada r) {
        jdbc.update("""
                INSERT INTO resposta (id_sub, id_questao, valor_escala, id_alternativa, texto)
                VALUES (?, ?, ?, ?, ?)
                """, idSub, r.idQuestao(), r.valorEscala(), r.idAlternativa(), r.texto());
    }
}
