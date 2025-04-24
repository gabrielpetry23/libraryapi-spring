package io.github.gabrielpetry23.libraryapi.controller.mappers;

import io.github.gabrielpetry23.libraryapi.controller.dtos.LivroDTO;
import io.github.gabrielpetry23.libraryapi.controller.dtos.ResultadoPesquisaLivroDTO;
import io.github.gabrielpetry23.libraryapi.model.Livro;
import io.github.gabrielpetry23.libraryapi.repository.AutorRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = AutorMapper.class) // Pode usar o AutorMapper para mapear o autor
public abstract class LivroMapper { // Precisa ser abstract para conseguir usar o @Autowired no AutorRepository e não dar erro de circularidade e de instanciação

    @Autowired
    AutorRepository autorRepository; // Para conseguir chamar o findById para converter o autor, pois no dto ele vem como id (UUID) e não como objeto Autor

    //@Mapping(source = "dataPublicacao", target = "dataPublicacao")
    @Mapping(target = "autor", expression = "java(autorRepository.findById(dto.idAutor()).orElse(null))") // convertendo o id do autor para o objeto Autor
    public abstract Livro toEntity(LivroDTO dto);

    public abstract ResultadoPesquisaLivroDTO toDTO(Livro livro);
}
