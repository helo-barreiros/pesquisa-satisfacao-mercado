package br.uel.pesquisa.dto;

import java.util.List;

public record ConviteRequest(List<Integer> usuarios, List<Integer> grupos) {
}