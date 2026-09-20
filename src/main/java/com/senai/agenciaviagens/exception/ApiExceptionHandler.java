package com.senai.agenciaviagens.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException excecao,
                                                                   HttpServletRequest requisicao) {
        return montar(HttpStatus.NOT_FOUND, "Recurso nao encontrado", excecao.getMessage(), requisicao, null);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> tratarRegraNegocio(RegraNegocioException excecao,
                                                           HttpServletRequest requisicao) {
        return montar(HttpStatus.BAD_REQUEST, "Requisicao invalida", excecao.getMessage(), requisicao, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> tratarAcessoNegado(AccessDeniedException excecao,
                                                           HttpServletRequest requisicao) {
        return montar(HttpStatus.FORBIDDEN, "Acesso negado",
                "O perfil autenticado nao possui permissao para esta operacao", requisicao, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException excecao,
                                                        HttpServletRequest requisicao) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError erro : excecao.getBindingResult().getFieldErrors()) {
            campos.put(erro.getField(), erro.getDefaultMessage());
        }
        return montar(HttpStatus.BAD_REQUEST, "Validacao falhou",
                "Existem campos invalidos na requisicao", requisicao, campos);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroInesperado(Exception excecao, HttpServletRequest requisicao) {
        LOG.error("Erro inesperado ao processar {} {}", requisicao.getMethod(), requisicao.getRequestURI(), excecao);
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Ocorreu um erro inesperado ao processar a requisicao", requisicao, null);
    }

    private ResponseEntity<ErroResponse> montar(HttpStatus status, String erro, String mensagem,
                                                HttpServletRequest requisicao, Map<String, String> campos) {
        ErroResponse corpo = new ErroResponse(
                LocalDateTime.now(),
                status.value(),
                erro,
                mensagem,
                requisicao.getRequestURI(),
                campos);
        return ResponseEntity.status(status).body(corpo);
    }
}
