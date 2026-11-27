package br.uel.pesquisa.model;

import java.util.List;

public record PesquisaDetalhe(Pesquisa pesquisa, List<ItemPesquisa> questoes, boolean temRespostas) {
}