package br.uel.pesquisa.model;

public record TipoQuestao(int id, String codigo, String descricao) {

    public static final String ESCALA = "ESCALA";
    public static final String MULTIPLA_ESCOLHA = "MULTIPLA_ESCOLHA";
    public static final String TEXTO_LIVRE = "TEXTO_LIVRE";
}