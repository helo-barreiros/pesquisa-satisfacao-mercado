package br.uel.pesquisa.dto;

import java.time.LocalDateTime;

public record NovaPesquisa(String titulo, String descricao, LocalDateTime inicio, LocalDateTime fim,
                           Boolean anonima) {
}