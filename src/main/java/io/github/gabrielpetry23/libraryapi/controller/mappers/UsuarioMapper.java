package io.github.gabrielpetry23.libraryapi.controller.mappers;

import io.github.gabrielpetry23.libraryapi.controller.dtos.UsuarioDTO;
import io.github.gabrielpetry23.libraryapi.model.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(UsuarioDTO dto);
}
