package br.uel.pesquisa.model;

import java.time.LocalDateTime;
import java.util.List;

public record Questao(int id, String enunciado, boolean ativa, LocalDateTime dataCriacao,
                      int idTipo, String tipo, int idTema, String tema, int usos,
                      List<Alternativa> alternativas) {

    public Questao comAlternativas(List<Alternativa> novas) {
        return new Questao(id, enunciado, ativa, dataCriacao, idTipo, tipo, idTema, tema, usos, novas);
    }
}