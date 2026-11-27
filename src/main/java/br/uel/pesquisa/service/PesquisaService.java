package br.uel.pesquisa.service;

import br.uel.pesquisa.dao.ConviteDao;
import br.uel.pesquisa.dao.PesquisaDao;
import br.uel.pesquisa.dto.ConviteRequest;
import br.uel.pesquisa.dto.ItemNovo;
import br.uel.pesquisa.dto.NovaPesquisa;
import br.uel.pesquisa.model.Pesquisa;
import br.uel.pesquisa.model.PesquisaDetalhe;
import br.uel.pesquisa.model.Usuario;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


//Cadastro de pesquisas
@Service
public class PesquisaService {

    private final PesquisaDao pesquisaDao;
    private final ConviteDao conviteDao;

    public PesquisaService(PesquisaDao pesquisaDao, ConviteDao conviteDao) {
        this.pesquisaDao = pesquisaDao;
        this.conviteDao = conviteDao;
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
    public PesquisaDetalhe detalhar(int idPesquisa) {
        Pesquisa p = buscar(idPesquisa);
        return new PesquisaDetalhe(p, pesquisaDao.listarQuestoes(idPesquisa),
                pesquisaDao.contarSubmissoes(idPesquisa) > 0);
    }

    @Transactional
    public void definirQuestoes(int idPesquisa, List<ItemNovo> itens) {
        buscar(idPesquisa);
        if (pesquisaDao.contarSubmissoes(idPesquisa) > 0) {
            throw new RegraNegocio("Esta pesquisa já tem respostas: as questões não podem mais ser alteradas.");
        }
        if (itens == null || itens.isEmpty()) {
            throw new RegraNegocio("Escolha pelo menos uma questão.");
        }
        Set<Integer> vistos = new HashSet<>();
        for (ItemNovo item : itens) {
            if (!vistos.add(item.idQuestao())) {
                throw new RegraNegocio("A questão " + item.idQuestao() + " foi escolhida duas vezes.");
            }
        }
        pesquisaDao.removerQuestoes(idPesquisa);
        for (int i = 0; i < itens.size(); i++) {
            ItemNovo item = itens.get(i);
            pesquisaDao.inserirQuestao(idPesquisa, item.idQuestao(), i + 1, item.obrigatoria());
        }
    }

    @Transactional
    public int convidar(int idPesquisa, ConviteRequest pedido) {
        buscar(idPesquisa);
        int novos = 0;
        if (pedido.grupos() != null) {
            for (Integer idGrupo : pedido.grupos()) {
                novos += conviteDao.convidarGrupo(idPesquisa, idGrupo);
            }
        }
        if (pedido.usuarios() != null) {
            for (Integer idUsuario : pedido.usuarios()) {
                novos += conviteDao.convidarUsuario(idPesquisa, idUsuario);
            }
        }
        return novos;
    }

    public void removerConvite(int idPesquisa, int idUsuario) {
        if (conviteDao.remover(idPesquisa, idUsuario) == 0) {
            throw new RegraNegocio("Só é possível retirar o convite de quem ainda não respondeu.");
        }
    }
}