package br.uel.pesquisa.config;

import br.uel.pesquisa.model.Usuario;
import jakarta.servlet.http.HttpSession;

public final class Sessao {

    public static final String USUARIO = "usuarioLogado";

    private Sessao() {
    }

    public static Usuario usuario(HttpSession sessao) {
        return sessao == null ? null : (Usuario) sessao.getAttribute(USUARIO);
    }
}
