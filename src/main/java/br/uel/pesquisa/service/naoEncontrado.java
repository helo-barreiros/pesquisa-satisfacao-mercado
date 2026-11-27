package br.uel.pesquisa.service;

public class naoEncontrado extends RuntimeException {
    public naoEncontrado(String message) {
        super(message);
    }
}
