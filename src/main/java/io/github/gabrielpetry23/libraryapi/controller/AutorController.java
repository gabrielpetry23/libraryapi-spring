package io.github.gabrielpetry23.libraryapi.controller;

import io.github.gabrielpetry23.libraryapi.controller.dtos.AutorDTO;
import io.github.gabrielpetry23.libraryapi.controller.dtos.ErroResposta;
import io.github.gabrielpetry23.libraryapi.controller.mappers.AutorMapper;
import io.github.gabrielpetry23.libraryapi.exceptions.OperacaoNaoPermitidaException;
import io.github.gabrielpetry23.libraryapi.exceptions.RegistroDuplicadoException;
import io.github.gabrielpetry23.libraryapi.model.Autor;
import io.github.gabrielpetry23.libraryapi.service.AutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/autores")
@RequiredArgsConstructor
@Tag(name = "Autor", description = "Endpoints para gerenciar autores") // Swagger
@Slf4j // Log4j
public class AutorController implements GenericController {

    private final AutorService service;
    private final AutorMapper mapper;

    @PostMapping
    //@RequestMapping(method = RequestMethod.POST) // same
    // @Valid faz a validação dos campos que estão anotados com @NotNull, @NotBlank, etc.
    // GlobalExceptionHandler vai tratar os erros de validação
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Salvar Autor", description = "Endpoint para salvar um autor")
    @ApiResponses({
             @ApiResponse(responseCode = "201", description = "Autor salvo com sucesso"),
             @ApiResponse(responseCode = "422", description = "Erro de validação"),
             @ApiResponse(responseCode = "409", description = "Autor já cadastrado")
    })
    public ResponseEntity<Object> salvar(@RequestBody @Valid AutorDTO dto) {

        log.info("Salvando autor: {}", dto.nome()); // deve estar configurado corretamente no application.yml/properties
        // Mapear o DTO para a entidade Autor
        Autor autor = mapper.toEntity(dto);
        // Enviar a entidade Autor para o serviço validar e salvar no banco
        service.salvar(autor);

        // Criar url para acesso dos dados do autor
        // http://localhost:8080/autores/76a0b8e-4c2f-4d3b-8f5c-1a2b3c4d5e6f
        // .fromCurrentRequest() pega a url atual da requisição
        // .path("/{id}") adiciona o id do autor na url
        // .buildAndExpand(autor.getId()) substitui o {id} pelo id do autor
        // .toUri() converte para URI
            /*
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{id}")
                    .buildAndExpand(autor.getId()).toUri();
             */

        // Outra forma de fazer, utilizando GenericController para padronizar
        URI location = gerarHeaderLocation(autor.getId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutorDTO> obterDetalhes(@PathVariable("id") String id) {
        var idAutor = UUID.fromString(id);
        Optional<Autor> autorOptional = service.findById(idAutor);

        return service
                .findById(idAutor)
                .map(autor -> {
                    AutorDTO dto = mapper.toDTO(autor);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> ResponseEntity.notFound().build());

        // Outra forma de fazer
        /*

        if (autorOptional.isPresent()) {
            Autor autor = autorOptional.get();
            AutorDTO autorDTO = mapper.toDTO(autor);
            return ResponseEntity.ok(autorDTO);
        }
        return ResponseEntity.notFound().build();

         */
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable("id") String id) {
        try {
            var idAutor = UUID.fromString(id);
            Optional<Autor> autorOptional = service.findById(idAutor);
            if (autorOptional.isEmpty()) {
                return ResponseEntity.notFound().build();

            }

            //service.deletarById(idAutor); works too
            service.deletar(autorOptional.get());
            return ResponseEntity.noContent().build();
        } catch (OperacaoNaoPermitidaException e) {
            var erroDTO = ErroResposta.respostaPadrao(e.getMessage());
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }
    }

    @GetMapping("/pesquisar")
    public ResponseEntity<List<AutorDTO>> pesquisar(
            @RequestParam(value = "nome", required = false) String nome
            , @RequestParam(value = "nacionalidade", required = false) String nacionalidade) {

//        log.trace("Pesquisa autores:");
//        log.debug("Pesquisa autores:");
//        log.info("Pesquisa autores:");
//        log.warn("Pesquisa autores:");
//        log.error("Pesquisa autores:");

        List<Autor> autores = service.pesquisa(nome, nacionalidade);
        List<AutorDTO> autoresDTO = autores.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(autoresDTO);
    }

    @GetMapping("/pesquisarExample")
    public ResponseEntity<List<AutorDTO>> pesquisarPorExample(
            @RequestParam(value = "nome", required = false) String nome
            , @RequestParam(value = "nacionalidade", required = false) String nacionalidade) {
        List<Autor> autores = service.pesquisaByExample(nome, nacionalidade);
        List<AutorDTO> autoresDTO = autores.stream()
                .map(a -> new AutorDTO(
                        a.getId(),
                        a.getNome(),
                        a.getDataNascimento(),
                        a.getNacionalidade()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(autoresDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AutorDTO>> getAll() {
        List<Autor> autores = service.listar();
        List<AutorDTO> autoresDTO = autores.stream()
                .map(a -> new AutorDTO(
                        a.getId(),
                        a.getNome(),
                        a.getDataNascimento(),
                        a.getNacionalidade()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(autoresDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable("id") String id, @RequestBody @Valid AutorDTO autorDTO) {
        try {
            var idAutor = UUID.fromString(id);
            Optional<Autor> autorOptional = service.findById(idAutor);
            if (autorOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Autor autor = autorOptional.get();
            autor.setNome(autorDTO.nome());
            autor.setNacionalidade(autorDTO.nacionalidade());
            autor.setDataNascimento(autorDTO.dataNascimento());

            service.atualizar(autor);
            return ResponseEntity.noContent().build();
        } catch (RegistroDuplicadoException e) {
            var erroDTO = ErroResposta.conflito(e.getMessage());
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }
    }

    @GetMapping("/nome")
    public ResponseEntity<List<AutorDTO>> buscarAutorPeloNome(@RequestParam(value = "nome", required = false) String nome) {
        List<Autor> autores = service.listarPeloNome(nome);
        List<AutorDTO> autoresDTO = autores.stream()
                .map(a -> new AutorDTO(
                        a.getId(),
                        a.getNome(),
                        a.getDataNascimento(),
                        a.getNacionalidade()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(autoresDTO);
    }

    @DeleteMapping("/all")
    public ResponseEntity<Object> deletarTodos() {
        try {
            service.deletarTodos();
            return ResponseEntity.noContent().build();
        } catch (OperacaoNaoPermitidaException e) {
            var erroDTO = ErroResposta.respostaPadrao(e.getMessage());
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }
    }

    @DeleteMapping("/allWithNoBooks")
    public ResponseEntity<Object> deletarTodosSemLivros() {
        service.deletarTodosAutoresQueNaoTemLivro();
        return ResponseEntity.noContent().build();
    }
}

