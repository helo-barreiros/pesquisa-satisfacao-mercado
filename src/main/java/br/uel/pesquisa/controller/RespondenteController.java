package br.uel.pesquisa.controller;

import br.uel.pesquisa.config.Sessao;
import br.uel.pesquisa.dao.ConviteDao;
import br.uel.pesquisa.model.MinhaPesquisa;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Lado usuário
@RestController
@RequestMapping("/api/minhas-pesquisas")
public class RespondenteController {

    private final ConviteDao conviteDao;

    public RespondenteController(ConviteDao conviteDao) {
        this.conviteDao = conviteDao;
    }

    @GetMapping
    public List<MinhaPesquisa> minhas(HttpSession sessao) {
        return conviteDao.listarDoUsuario(Sessao.usuario(sessao).id());
    }
}