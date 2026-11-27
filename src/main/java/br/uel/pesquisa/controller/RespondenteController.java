package br.uel.pesquisa.controller;

import br.uel.pesquisa.config.Sessao;
import br.uel.pesquisa.dao.ConviteDao;
import br.uel.pesquisa.dto.RespostaEnviada;
import br.uel.pesquisa.model.Formulario;
import br.uel.pesquisa.model.MinhaPesquisa;
import br.uel.pesquisa.service.RespostaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Lado usuário
@RestController
@RequestMapping("/api/minhas-pesquisas")
public class RespondenteController {

    private final ConviteDao conviteDao;
    private final RespostaService respostaService;

    public RespondenteController(ConviteDao conviteDao, RespostaService respostaService) {
        this.conviteDao = conviteDao;
        this.respostaService = respostaService;
    }

    @GetMapping
    public List<MinhaPesquisa> minhas(HttpSession sessao) {
        return conviteDao.listarDoUsuario(Sessao.usuario(sessao).id());
    }

    @GetMapping("/{id}")
    public Formulario formulario(@PathVariable("id") int id, HttpSession sessao) {
        return respostaService.formulario(id, Sessao.usuario(sessao));
    }

    @PostMapping("/{id}/respostas")
    @ResponseStatus(HttpStatus.CREATED)
    public void responder(@PathVariable("id") int id, @RequestBody List<RespostaEnviada> respostas,
                          HttpSession sessao) {
        respostaService.responder(id, Sessao.usuario(sessao), respostas);
    }
}
