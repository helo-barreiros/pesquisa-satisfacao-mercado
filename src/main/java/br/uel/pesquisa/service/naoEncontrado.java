package br.uel.pesquisa.service;

public class NaoEncontrado extends RuntimeException {
    public NaoEncontrado(String message) {
        super(message);
    }
}
