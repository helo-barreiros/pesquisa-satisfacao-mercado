package br.uel.pesquisa.model;

import java.time.LocalDateTime;

// se e quando a pessoa respondeu, nunca o que respondeu
public record Convidado(int idUsuario, String nome, String email, String grupo,
                        LocalDateTime dtConvite, LocalDateTime dtResp) {
}