package br.uel.pesquisa.model;

import java.util.List;
import java.util.Map;

public record Formulario(MinhaPesquisa pesquisa, List<ItemPesquisa> questoes,
                         Map<Integer, List<Alternativa>> alternativas) {
}