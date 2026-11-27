package br.uel.pesquisa.controller;

import br.uel.pesquisa.config.Sessao;
import br.uel.pesquisa.dao.PesquisaDao;
import br.uel.pesquisa.dto.ItemNovo;
import br.uel.pesquisa.dto.NovaPesquisa;
import br.uel.pesquisa.model.Pesquisa;
import br.uel.pesquisa.model.PesquisaDetalhe;
import br.uel.pesquisa.service.PesquisaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/{id}")
    public PesquisaDetalhe detalhar(@PathVariable("id") int id) {
        return pesquisaService.detalhar(id);
    }

    @PutMapping("/{id}/questoes")
    public PesquisaDetalhe definirQuestoes(@PathVariable("id") int id, @RequestBody List<ItemNovo> itens) {
        pesquisaService.definirQuestoes(id, itens);
        return pesquisaService.detalhar(id);
    }
}