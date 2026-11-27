package br.uel.pesquisa.model;

import java.io.Serializable;

public record Usuario(int id, String nome, String email, String papel, Integer idGrupo, String grupo)
        implements Serializable {

    public boolean ehAdmin() {
        return "ADMIN".equals(papel);
    }
}
