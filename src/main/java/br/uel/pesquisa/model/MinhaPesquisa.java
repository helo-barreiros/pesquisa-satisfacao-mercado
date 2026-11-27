package br.uel.pesquisa.model;

import java.time.LocalDateTime;

//os dados da pesquisa + o convite do usuário
public record MinhaPesquisa(int id, String titulo, String descricao, LocalDateTime inicio, LocalDateTime fim,
                            boolean anonima, int qtdQuestoes, LocalDateTime dtConvite, LocalDateTime dtResp) {
}