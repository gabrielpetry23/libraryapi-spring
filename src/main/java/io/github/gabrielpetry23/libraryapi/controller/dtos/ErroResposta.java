package io.github.gabrielpetry23.libraryapi.controller.dtos;

import  org.springframework.http.HttpStatus;

import java.util.List;

public record ErroResposta(int status, String mensagem, List<ErroCampo> erros) {

    public static ErroResposta respostaPadrao(String msg) {
        return new ErroResposta(HttpStatus.BAD_REQUEST.value(), msg, List.of());
    }

    public static ErroResposta conflito(String msg) {
        return new ErroResposta(HttpStatus.CONFLICT.value(), msg, List.of());
    }


}
