package br.uel.pesquisa.controller;

import br.uel.pesquisa.dao.UsuarioDao;
import br.uel.pesquisa.model.Usuario;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class ApoioController {

    private final UsuarioDao usuarioDao;

    public ApoioController(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    @GetMapping("/respondentes")
    public List<Usuario> respondentes() {
        return usuarioDao.listarRespondentes();
    }
}