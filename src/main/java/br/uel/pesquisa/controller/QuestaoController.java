package br.uel.pesquisa.controller;

import br.uel.pesquisa.dao.QuestaoDao;
import br.uel.pesquisa.dto.NovaQuestao;
import br.uel.pesquisa.model.Questao;
import br.uel.pesquisa.service.QuestaoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

//listagem e cadastro
@RestController
@RequestMapping("/api/admin/questoes")
public class QuestaoController {

    private final QuestaoDao questaoDao;
    private final QuestaoService questaoService;

    public QuestaoController(QuestaoDao questaoDao, QuestaoService questaoService) {
        this.questaoDao = questaoDao;
        this.questaoService = questaoService;
    }

    @GetMapping
    public List<Questao> listar(@RequestParam(name = "tema", required = false) Integer tema,
                                @RequestParam(name = "busca", required = false) String busca) {
        return questaoDao.listar(tema, busca);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Integer> criar(@RequestBody NovaQuestao nova) {
        return Map.of("id", questaoService.criar(nova));
    }
}