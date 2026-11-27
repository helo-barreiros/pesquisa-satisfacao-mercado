package br.uel.pesquisa.controller;

import br.uel.pesquisa.config.Sessao;
import br.uel.pesquisa.dao.ConviteDao;
import br.uel.pesquisa.dao.PesquisaDao;
import br.uel.pesquisa.dto.ConviteRequest;
import br.uel.pesquisa.dto.ItemNovo;
import br.uel.pesquisa.dto.NovaPesquisa;
import br.uel.pesquisa.model.Convidado;
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
    private final ConviteDao conviteDao;
    private final PesquisaService pesquisaService;

    public PesquisaController(PesquisaDao pesquisaDao, ConviteDao conviteDao, PesquisaService pesquisaService) {
        this.pesquisaDao = pesquisaDao;
        this.conviteDao = conviteDao;
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
    @GetMapping("/{id}/convites")
    public List<Convidado> convidados(@PathVariable("id") int id) {
        pesquisaService.buscar(id);
        return conviteDao.listar(id);
    }

    @PostMapping("/{id}/convites")
    public Map<String, Integer> convidar(@PathVariable("id") int id, @RequestBody ConviteRequest pedido) {
        return Map.of("novos", pesquisaService.convidar(id, pedido));
    }

    @DeleteMapping("/{id}/convites/{idUsuario}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerConvite(@PathVariable("id") int id, @PathVariable("idUsuario") int idUsuario) {
        pesquisaService.removerConvite(id, idUsuario);
    }
}