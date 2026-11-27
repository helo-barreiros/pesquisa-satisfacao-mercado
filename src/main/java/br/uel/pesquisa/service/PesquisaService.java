package br.uel.pesquisa.service;

import br.uel.pesquisa.dao.PesquisaDao;
import br.uel.pesquisa.dto.NovaPesquisa;
import br.uel.pesquisa.model.Pesquisa;
import br.uel.pesquisa.model.Usuario;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

//Cadastro de pesquisas
@Service
public class PesquisaService {

    private final PesquisaDao pesquisaDao;

    public PesquisaService(PesquisaDao pesquisaDao) {
        this.pesquisaDao = pesquisaDao;
    }

    public Pesquisa buscar(int idPesquisa) {
        return pesquisaDao.buscar(idPesquisa)
                .orElseThrow(() -> new NaoEncontrado("Pesquisa " + idPesquisa + " não encontrada."));
    }

    public int criar(NovaPesquisa nova, Usuario admin) {
        String titulo = nova.titulo() == null ? "" : nova.titulo().trim();
        if (titulo.length() < 3) {
            throw new RegraNegocio("O título precisa ter pelo menos 3 caracteres.");
        }
        if (nova.inicio() == null || nova.fim() == null) {
            throw new RegraNegocio("Informe a data e a hora de início e de fim.");
        }
        if (!nova.fim().isAfter(nova.inicio())) {
            throw new RegraNegocio("O fim precisa ser depois do início.");
        }
        String descricao = nova.descricao() == null || nova.descricao().isBlank() ? null : nova.descricao().trim();
        boolean anonima = nova.anonima() == null || nova.anonima();
        try {
            return pesquisaDao.inserir(titulo, descricao, nova.inicio(), nova.fim(), anonima, admin.id());
        } catch (DuplicateKeyException e) {
            throw new RegraNegocio("Já existe uma pesquisa com esse título.");
        }
    }
}