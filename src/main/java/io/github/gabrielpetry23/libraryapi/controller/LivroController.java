package io.github.gabrielpetry23.libraryapi.controller;

import io.github.gabrielpetry23.libraryapi.controller.dtos.ErroResposta;
import io.github.gabrielpetry23.libraryapi.controller.dtos.LivroDTO;
import io.github.gabrielpetry23.libraryapi.controller.dtos.ResultadoPesquisaLivroDTO;
import io.github.gabrielpetry23.libraryapi.controller.mappers.LivroMapper;
import io.github.gabrielpetry23.libraryapi.exceptions.RegistroDuplicadoException;
import io.github.gabrielpetry23.libraryapi.model.GeneroLivro;
import io.github.gabrielpetry23.libraryapi.model.Livro;
import io.github.gabrielpetry23.libraryapi.service.LivroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
//@PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')") // Para aplicar as permissões em todos os métodos dessa classe
public class LivroController implements GenericController {

    private final LivroService service;
    private final LivroMapper mapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public ResponseEntity<Object> salvar(@RequestBody @Valid LivroDTO dto) {

        // Mapear o DTO para a entidade Livro
        Livro livro = mapper.toEntity(dto);
        // Enviar a entidade Livro para o serviço validar e salvar no banco
        service.salvar(livro);
        // Criar url para acesso dos dados do livro no header location
        URI url = gerarHeaderLocation(livro.getId());
        // Retornar o livro salvo com o status 201 (created) e o header location com a url do livro
        return ResponseEntity.created(url).build();
    }

    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<ResultadoPesquisaLivroDTO> obterDetalhes(@PathVariable("id") String id) {
        return service.findById(UUID.fromString(id))
                .map(livro -> {
                    var dto = mapper.toDTO(livro);
                    return ResponseEntity.ok(dto);
                }).orElseGet( () -> {
                    return ResponseEntity.notFound().build();
                });
    }

    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable("id") String id) {
        return service.findById(UUID.fromString(id))
                .map(livro -> {
                    service.deletar(livro);
                    return ResponseEntity.noContent().build();
                }).orElseGet( () -> {
                    return ResponseEntity.notFound().build();
                });
    }

    /*
    @GetMapping("/pesquisar")
    public ResponseEntity<List<ResultadoPesquisaLivroDTO>> pesquisar(
            @RequestParam(value = "isbn", required = false) String isbn,
            @RequestParam(value = "nomeAutor", required = false) String nomeAutor,
            @RequestParam(value = "titulo", required = false) String titulo,
            @RequestParam(value = "genero", required = false) GeneroLivro genero,
            @RequestParam(value = "anoPublicacao", required = false) Integer anoPublicacao,
    ) {
        var result = service.pesquisa(isbn, nomeAutor, titulo, genero, anoPublicacao);
        var lista = result.stream().map(mapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }
     */


    // Com paginação
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    @GetMapping("/pesquisar")
    public ResponseEntity<Page<ResultadoPesquisaLivroDTO>> pesquisaPaginada(
            @RequestParam(value = "isbn", required = false) String isbn,
            @RequestParam(value = "nomeAutor", required = false) String nomeAutor,
            @RequestParam(value = "titulo", required = false) String titulo,
            @RequestParam(value = "genero", required = false) GeneroLivro genero,
            @RequestParam(value = "anoPublicacao", required = false) Integer anoPublicacao,
            @RequestParam(value = "pagina", defaultValue = "0") Integer pagina,
            @RequestParam(value = "tamPagina", defaultValue = "10") Integer tamPagina
    ) {
        Page<Livro> paginaResult = service.pesquisaPaginada(isbn, nomeAutor, titulo, genero, anoPublicacao, pagina, tamPagina);
        Page<ResultadoPesquisaLivroDTO> result = paginaResult.map(mapper::toDTO);

        return ResponseEntity.ok(result);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public ResponseEntity<Object> atualizar(@PathVariable("id") String id, @RequestBody @Valid LivroDTO dto) {
        return service.findById(UUID.fromString(id))
                .map(livro -> {
                    Livro entidadeAux = mapper.toEntity(dto);
                    livro.setDataPublicacao(entidadeAux.getDataPublicacao());
                    livro.setGenero(entidadeAux.getGenero());
                    livro.setIsbn(entidadeAux.getIsbn());
                    livro.setTitulo(entidadeAux.getTitulo());
                    livro.setAutor(entidadeAux.getAutor());
                    livro.setPreco(entidadeAux.getPreco());

                    service.atualizar(livro);

                    return ResponseEntity.noContent().build();
                }).orElseGet( () -> {
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/all-titulos")
    public String getAllTitulosDosLivros() {
        List<Livro> livros = service.findAll();
        return livros.stream()
                .map(Livro::getTitulo)
                .collect(Collectors.joining(", "));
    }
}
