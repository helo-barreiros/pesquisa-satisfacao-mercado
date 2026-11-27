package br.uel.pesquisa.config;

import br.uel.pesquisa.service.naoEncontrado;
import br.uel.pesquisa.service.regraNegocio;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@RestControllerAdvice
public class tratarErros {

    private static final Logger log = LoggerFactory.getLogger(tratarErros.class);

    @ExceptionHandler(regraNegocio.class)
    public ResponseEntity<Map<String, String>> regra(regraNegocio e) {
        return erro(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(naoEncontrado.class)
    public ResponseEntity<Map<String, String>> naoEncontrado(naoEncontrado e) {
        return erro(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> banco(DataAccessException e) {
        log.warn("Erro do banco: {}", e.getMostSpecificCause().getMessage());
        Throwable causa = e.getMostSpecificCause();
        String mensagem = causa.getMessage();
        if (causa instanceof PSQLException p && p.getServerErrorMessage() != null) {
            mensagem = p.getServerErrorMessage().getMessage();
        }
        return erro(HttpStatus.BAD_REQUEST, "O banco recusou a operação: " + mensagem);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, String>> dadosInvalidos(Exception e) {
        return erro(HttpStatus.BAD_REQUEST, "Dados inválidos. Confira os campos do formulário.");
    }

    private ResponseEntity<Map<String, String>> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("erro", mensagem));
    }
}
