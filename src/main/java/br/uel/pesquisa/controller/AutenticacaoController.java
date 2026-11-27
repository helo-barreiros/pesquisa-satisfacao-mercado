package br.uel.pesquisa.controller;

import br.uel.pesquisa.config.Sessao;
import br.uel.pesquisa.dao.UsuarioDao;
import br.uel.pesquisa.dto.LoginRequest;
import br.uel.pesquisa.model.Usuario;
import br.uel.pesquisa.service.RegraNegocio;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Login simples: o usuário se identifica só pelo e-mail. */
@RestController
@RequestMapping("/api")
public class AutenticacaoController {

    private final UsuarioDao usuarioDao;

    public AutenticacaoController(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    @PostMapping("/login")
    public Usuario login(@RequestBody LoginRequest pedido, HttpSession sessao) {
        if (pedido.email() == null || pedido.email().isBlank()) {
            throw new RegraNegocio("Informe o e-mail.");
        }
        Usuario usuario = usuarioDao.buscarPorEmail(pedido.email())
                .orElseThrow(() -> new RegraNegocio("E-mail não cadastrado."));
        sessao.setAttribute(Sessao.USUARIO, usuario);
        return usuario;
    }

    @GetMapping("/eu")
    public Usuario eu(HttpSession sessao) {
        return Sessao.usuario(sessao);
    }

    @PostMapping("/logout")
    public void logout(HttpSession sessao) {
        sessao.invalidate();
    }
}
