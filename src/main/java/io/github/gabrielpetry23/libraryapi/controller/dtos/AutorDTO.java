package io.github.gabrielpetry23.libraryapi.controller.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDate;
import java.util.UUID;

@Schema(name = "Autor", description = "Representa um autor")
public record AutorDTO(

        UUID id,
        @NotBlank(message = "Campo obrigatório") // NotNull para Strings, não pode ser nula nem vazia
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
        @Schema(name = "nome")
        String nome,
        @NotNull(message = "Campo obrigatório")
        @Past(message = "A data de nascimento deve ser uma data passada")
        @Schema(name = "data de nascimento")
        LocalDate dataNascimento,
        @NotBlank(message = "Campo obrigatório")
        @Size(min = 2, max = 50, message = "A nacionalidade deve ter entre 2 e 50 caracteres")
        String nacionalidade) {
}
