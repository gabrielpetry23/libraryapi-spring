package io.github.gabrielpetry23.libraryapi.controller.mappers;

import io.github.gabrielpetry23.libraryapi.controller.dtos.AutorDTO;
import io.github.gabrielpetry23.libraryapi.model.Autor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AutorMapper {

    // MapStruct não sabe como converter LocalDate para String
    @Mapping(source = "dataNascimento", target = "dataNascimento")
    Autor toEntity(AutorDTO autorDTO);

    @Mapping(source = "dataNascimento", target = "dataNascimento")
    AutorDTO toDTO(Autor autor);
}
