package br.uel.pesquisa.service;

import br.uel.pesquisa.dao.ConviteDao;
import br.uel.pesquisa.dao.PesquisaDao;
import br.uel.pesquisa.dao.QuestaoDao;
import br.uel.pesquisa.dao.SubmissaoDao;
import br.uel.pesquisa.dto.RespostaEnviada;
import br.uel.pesquisa.model.Formulario;
import br.uel.pesquisa.model.ItemPesquisa;
import br.uel.pesquisa.model.MinhaPesquisa;
import br.uel.pesquisa.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

//Responder uma pesquisa (anonimato)
@Service
public class RespostaService {

    private final ConviteDao conviteDao;
    private final PesquisaDao pesquisaDao;
    private final QuestaoDao questaoDao;
    private final SubmissaoDao submissaoDao;

    public RespostaService(ConviteDao conviteDao, PesquisaDao pesquisaDao, QuestaoDao questaoDao,
                           SubmissaoDao submissaoDao) {
        this.conviteDao = conviteDao;
        this.pesquisaDao = pesquisaDao;
        this.questaoDao = questaoDao;
        this.submissaoDao = submissaoDao;
    }

    //O formulário só abre para quem foi convidado, ainda não respondeu e com a pesquisa aberta
    public Formulario formulario(int idPesquisa, Usuario usuario) {
        MinhaPesquisa p = conviteDao.buscarDoUsuario(usuario.id(), idPesquisa)
                .orElseThrow(() -> new NaoEncontrado("Você não foi convidado para esta pesquisa."));
        if (p.dtResp() != null) {
            throw new RegraNegocio("Você já respondeu esta pesquisa.");
        }
        LocalDateTime agora = LocalDateTime.now();
        if (agora.isBefore(p.inicio()) || agora.isAfter(p.fim())) {
            throw new RegraNegocio("Esta pesquisa não está aberta.");
        }
        return new Formulario(p, pesquisaDao.listarQuestoes(idPesquisa), questaoDao.alternativasPorQuestao());
    }

    //Grava tudo numa transação só
    @Transactional
    public void responder(int idPesquisa, Usuario usuario, List<RespostaEnviada> respostas) {
        Formulario form = formulario(idPesquisa, usuario);

        for (ItemPesquisa q : form.questoes()) {
            boolean respondeu = respostas.stream().anyMatch(r -> r.idQuestao() == q.idQuestao());
            if (q.obrigatoria() && !respondeu) {
                throw new RegraNegocio("Responda a questão " + q.ordem() + ".");
            }
        }

        // 1) marca no convite QUEM respondeu
        conviteDao.marcarRespondido(idPesquisa, usuario.id());
        // 2) cria a submissão e se a pesquisa é anônima não guarda o usuário (só o grupo)
        Integer quem = form.pesquisa().anonima() ? null : usuario.id();
        UUID idSub = submissaoDao.inserir(idPesquisa, quem, usuario.idGrupo());
        // 3) grava O QUE foi respondido (o banco confere nota 1-5 e a alternativa)
        for (RespostaEnviada r : respostas) {
            submissaoDao.inserirResposta(idSub, r);
        }
    }
}
