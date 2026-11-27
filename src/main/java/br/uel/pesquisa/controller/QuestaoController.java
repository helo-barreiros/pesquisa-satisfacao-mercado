package br.uel.pesquisa.controller;

import br.uel.pesquisa.dao.QuestaoDao;
import br.uel.pesquisa.model.Questao;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//listagem
@RestController
@RequestMapping("/api/admin/questoes")
public class QuestaoController {

    private final QuestaoDao questaoDao;

    public QuestaoController(QuestaoDao questaoDao) {
        this.questaoDao = questaoDao;
    }

    @GetMapping
    public List<Questao> listar(@RequestParam(name = "tema", required = false) Integer tema,
                                @RequestParam(name = "busca", required = false) String busca) {
        return questaoDao.listar(tema, busca);
    }
}