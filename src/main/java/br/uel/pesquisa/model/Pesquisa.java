package br.uel.pesquisa.model;

import java.time.LocalDateTime;

public record Pesquisa(int id, String titulo, String descricao, LocalDateTime inicio, LocalDateTime fim,
                       boolean anonima, int qtdQuestoes, int convidados, int responderam) {
}