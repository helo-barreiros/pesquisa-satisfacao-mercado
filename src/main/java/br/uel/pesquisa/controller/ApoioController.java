package br.uel.pesquisa.controller;

import br.uel.pesquisa.dao.GrupoDao;
import br.uel.pesquisa.dao.TemaDao;
import br.uel.pesquisa.dao.TipoQuestaoDao;
import br.uel.pesquisa.dao.UsuarioDao;
import br.uel.pesquisa.model.Grupo;
import br.uel.pesquisa.model.Tema;
import br.uel.pesquisa.model.TipoQuestao;
import br.uel.pesquisa.model.Usuario;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//Listas para preencher os campos de seleção /
@RestController
@RequestMapping("/api/admin")
public class ApoioController {

    private final TemaDao temaDao;
    private final TipoQuestaoDao tipoDao;
    private final GrupoDao grupoDao;
    private final UsuarioDao usuarioDao;

    public ApoioController(TemaDao temaDao, TipoQuestaoDao tipoDao, GrupoDao grupoDao, UsuarioDao usuarioDao) {
        this.temaDao = temaDao;
        this.tipoDao = tipoDao;
        this.grupoDao = grupoDao;
        this.usuarioDao = usuarioDao;
    }

    @GetMapping("/temas")
    public List<Tema> temas() {
        return temaDao.listar();
    }

    @GetMapping("/tipos")
    public List<TipoQuestao> tipos() {
        return tipoDao.listar();
    }

    @GetMapping("/grupos")
    public List<Grupo> grupos() {
        return grupoDao.listar();
    }

    @GetMapping("/respondentes")
    public List<Usuario> respondentes() {
        return usuarioDao.listarRespondentes();
    }
}