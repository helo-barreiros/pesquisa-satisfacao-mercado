package br.uel.pesquisa.service;

import br.uel.pesquisa.dao.QuestaoDao;
import br.uel.pesquisa.dao.TemaDao;
import br.uel.pesquisa.dao.TipoQuestaoDao;
import br.uel.pesquisa.dto.NovaQuestao;
import br.uel.pesquisa.model.TipoQuestao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//Cadastro de questões
@Service
public class QuestaoService {

    private final QuestaoDao questaoDao;
    private final TipoQuestaoDao tipoDao;
    private final TemaDao temaDao;

    public QuestaoService(QuestaoDao questaoDao, TipoQuestaoDao tipoDao, TemaDao temaDao) {
        this.questaoDao = questaoDao;
        this.tipoDao = tipoDao;
        this.temaDao = temaDao;
    }

    // cria questão e suas alternaticas se for de multipla escolha
    @Transactional
    public int criar(NovaQuestao nova) {
        String enunciado = nova.enunciado() == null ? "" : nova.enunciado().trim();
        if (enunciado.length() < 5) {
            throw new RegraNegocio("O enunciado precisa ter pelo menos 5 caracteres.");
        }
        if (nova.idTipo() == null) {
            throw new RegraNegocio("Escolha o tipo da questão.");
        }
        if (nova.idTema() == null || !temaDao.existe(nova.idTema())) {
            throw new RegraNegocio("Escolha o tema da questão.");
        }
        TipoQuestao tipo = tipoDao.buscar(nova.idTipo())
                .orElseThrow(() -> new RegraNegocio("Tipo de questão inválido."));

        // tira alternativas em branco
        List<String> alternativas = nova.alternativas() == null ? List.of()
                : nova.alternativas().stream().map(String::trim).filter(a -> !a.isEmpty()).toList();

        if (TipoQuestao.MULTIPLA_ESCOLHA.equals(tipo.codigo())) {
            if (alternativas.size() < 2) {
                throw new RegraNegocio("Uma questão de múltipla escolha precisa de pelo menos 2 alternativas.");
            }
            if (alternativas.stream().distinct().count() != alternativas.size()) {
                throw new RegraNegocio("As alternativas não podem se repetir.");
            }
        } else {
            alternativas = List.of(); // escala e texto livre não têm alternativas
        }

        int idQuestao = questaoDao.inserir(enunciado, tipo.id(), nova.idTema());
        for (int i = 0; i < alternativas.size(); i++) {
            questaoDao.inserirAlternativa(idQuestao, i + 1, alternativas.get(i));
        }
        return idQuestao;
    }
}