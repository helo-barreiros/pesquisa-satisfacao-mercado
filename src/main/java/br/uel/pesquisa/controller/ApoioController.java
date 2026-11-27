package br.uel.pesquisa.controller;

import br.uel.pesquisa.dao.TemaDao;
import br.uel.pesquisa.dao.TipoQuestaoDao;
import br.uel.pesquisa.model.Tema;
import br.uel.pesquisa.model.TipoQuestao;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//Listas de apoio para preencher os campos de seleção das telas de administração
@RestController
@RequestMapping("/api/admin")
public class ApoioController {

    private final TemaDao temaDao;
    private final TipoQuestaoDao tipoDao;

    public ApoioController(TemaDao temaDao, TipoQuestaoDao tipoDao) {
        this.temaDao = temaDao;
        this.tipoDao = tipoDao;
    }

    @GetMapping("/temas")
    public List<Tema> temas() {
        return temaDao.listar();
    }

    @GetMapping("/tipos")
    public List<TipoQuestao> tipos() {
        return tipoDao.listar();
    }
}