package br.uel.pesquisa.controller;

import br.uel.pesquisa.config.Sessao;
import br.uel.pesquisa.dao.PesquisaDao;
import br.uel.pesquisa.dto.NovaPesquisa;
import br.uel.pesquisa.model.Pesquisa;
import br.uel.pesquisa.service.PesquisaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/pesquisas")
public class PesquisaController {

    private final PesquisaDao pesquisaDao;
    private final PesquisaService pesquisaService;

    public PesquisaController(PesquisaDao pesquisaDao, PesquisaService pesquisaService) {
        this.pesquisaDao = pesquisaDao;
        this.pesquisaService = pesquisaService;
    }

    @GetMapping
    public List<Pesquisa> listar() {
        return pesquisaDao.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Integer> criar(@RequestBody NovaPesquisa nova, HttpSession sessao) {
        return Map.of("id", pesquisaService.criar(nova, Sessao.usuario(sessao)));
    }
}