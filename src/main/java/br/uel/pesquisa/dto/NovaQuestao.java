package br.uel.pesquisa.dto;

import java.util.List;

public record NovaQuestao(String enunciado, Integer idTipo, Integer idTema, List<String> alternativas) {
}