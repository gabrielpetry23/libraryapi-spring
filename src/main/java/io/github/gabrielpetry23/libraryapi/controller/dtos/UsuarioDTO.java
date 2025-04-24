package io.github.gabrielpetry23.libraryapi.controller.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UsuarioDTO(
        @NotBlank(message = "campo obrigatório")
        String login,
        @Email(message = "inválido")
        String email,
        @NotBlank(message = "campo obrigatório")
        String senha,
        List<String> roles) {
}
