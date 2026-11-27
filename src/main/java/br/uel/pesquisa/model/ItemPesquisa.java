package br.uel.pesquisa.model;

public record ItemPesquisa(int idQuestao, int ordem, boolean obrigatoria,
                           String enunciado, String tipo, String tema) {
}