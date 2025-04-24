package io.github.gabrielpetry23.libraryapi.controller.common;

import io.github.gabrielpetry23.libraryapi.controller.dtos.ErroCampo;
import io.github.gabrielpetry23.libraryapi.controller.dtos.ErroResposta;
import io.github.gabrielpetry23.libraryapi.exceptions.CampoInvalidoException;
import io.github.gabrielpetry23.libraryapi.exceptions.OperacaoNaoPermitidaException;
import io.github.gabrielpetry23.libraryapi.exceptions.RegistroDuplicadoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Tratamento de exceções personalizadas, padrão de projeto "Chain of Responsibility"
    // Centraliza o tratamento de exceções, evitando try catch em cada controller e duplicação de código
    // Além disso, permite retornar respostas padronizadas para o cliente
    // Utiliza @RestControllerAdvice para interceptar as exceções lançadas pelos controllers

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErroResposta handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        log.error("Erro de validação: {}", ex.getMessage());

        List<FieldError> fieldErrors = ex.getFieldErrors();
        List<ErroCampo> listaErros = fieldErrors
                .stream()
                .map(fe -> new ErroCampo(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());

        return new ErroResposta(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Erro de validação", listaErros);
    }

    @ExceptionHandler(RegistroDuplicadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResposta handleRegistroDuplicadoException(RegistroDuplicadoException ex) {
        return ErroResposta.conflito(ex.getMessage());
    }

    @ExceptionHandler(OperacaoNaoPermitidaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResposta handleOperacaoNaoPermitidaException(OperacaoNaoPermitidaException ex) {
        return ErroResposta.respostaPadrao(ex.getMessage());
    }

    @ExceptionHandler(CampoInvalidoException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErroResposta handleCampoInvalidoException(CampoInvalidoException ex) {
        return new ErroResposta(
                HttpStatus.UNPROCESSABLE_ENTITY.value(), "Erro de validação.", List.of(new ErroCampo(ex.getCampo(), ex.getMessage())));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErroResposta handleAcessDeniedException(AccessDeniedException ex) {
        return new ErroResposta(HttpStatus.FORBIDDEN.value(), "Acesso negado.", List.of());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErroResposta handleErroNaoTratado(RuntimeException ex) {

        log.error("Erro inesperado: {}", ex.getMessage());

        return new ErroResposta(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro inesperado! Entre em contato com o suporte. ", List.of());
    }
}
