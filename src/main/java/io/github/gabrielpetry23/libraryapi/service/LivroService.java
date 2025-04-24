package io.github.gabrielpetry23.libraryapi.service;

import io.github.gabrielpetry23.libraryapi.model.GeneroLivro;
import io.github.gabrielpetry23.libraryapi.model.Livro;
import io.github.gabrielpetry23.libraryapi.model.Usuario;
import io.github.gabrielpetry23.libraryapi.repository.LivroRepository;
import io.github.gabrielpetry23.libraryapi.repository.specs.LivroSpecs;
import io.github.gabrielpetry23.libraryapi.validators.LivroValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import io.github.gabrielpetry23.libraryapi.security.SecurityService;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroValidator validator;
    private final LivroRepository repository;
    private final SecurityService securityService;

    public Livro salvar(Livro livro) {
        validator.validar(livro);
        Usuario currentUser = securityService.getCurrentUser(); // pega o usuário logado
        livro.setUsuario(currentUser); // seta o id do usuário logado no livro
        return repository.save(livro);
    }

    public Optional<Livro> findById(UUID id) {
        return repository.findById(id);
    }

    public void deletar(Livro livro) {
        repository.delete(livro);
    }

    public List<Livro> pesquisa(String isbn, String nomeAutor, String titulo, GeneroLivro genero, Integer anoPublicacao) {

        /* select * from livro where isbn = :isbn and nomeAutor like :nomeAutor and titulo like :titulo and genero = :genero and anoPublicacao = :anoPublicacao
        Specification<Livro> specs = Specification.where(
                LivroSpecs.isbnEqual(isbn))
                .and(LivroSpecs.tituloLike(titulo))
                .and(LivroSpecs.anoPublicacaoEqual(anoPublicacao))
                .and(LivroSpecs.autorNomeLike(nomeAutor))
                .and(LivroSpecs.generoEqual(genero));
        */

        // select * from livro where 0 = 0 (sem filtro)
        Specification<Livro> specs = Specification.where((root, query, cb) -> cb.conjunction());
        if (isbn != null) {
            // query = query and isbn = :isbn
            // (select * from livro where 0 = 0 and isbn = :isbn)
            specs = specs.and(LivroSpecs.isbnEqual(isbn));
        }

        if (titulo != null) {
            // query = query and titulo like :titulo
            specs = specs.and(LivroSpecs.tituloLike(titulo));
        }

        if (anoPublicacao != null) {
            // query = query and anoPublicacao = :anoPublicacao
            specs = specs.and(LivroSpecs.anoPublicacaoEqual(anoPublicacao));
        }

        if (nomeAutor != null) {
            // query = query and autor.nome like :nomeAutor
            specs = specs.and(LivroSpecs.autorNomeLike(nomeAutor));
        }

        if (genero != null) {
            // query = query and genero = :genero
            specs = specs.and(LivroSpecs.generoEqual(genero));
        }

        return repository.findAll(specs);
    }

    public Page<Livro> pesquisaPaginada(
            String isbn,
            String nomeAutor,
            String titulo,
            GeneroLivro genero,
            Integer anoPublicacao,
            Integer pagina,
            Integer tamPagina) {

        // select * from livro where 0 = 0 (sem filtro)
        Specification<Livro> specs = Specification.where((root, query, cb) -> cb.conjunction());
        if (isbn != null) {
            // query = query and isbn = :isbn
            // (select * from livro where 0 = 0 and isbn = :isbn)
            specs = specs.and(LivroSpecs.isbnEqual(isbn));
        }

        if (titulo != null) {
            // query = query and titulo like :titulo
            specs = specs.and(LivroSpecs.tituloLike(titulo));
        }

        if (anoPublicacao != null) {
            // query = query and anoPublicacao = :anoPublicacao
            specs = specs.and(LivroSpecs.anoPublicacaoEqual(anoPublicacao));
        }

        if (nomeAutor != null) {
            // query = query and autor.nome like :nomeAutor
            specs = specs.and(LivroSpecs.autorNomeLike(nomeAutor));
        }

        if (genero != null) {
            // query = query and genero = :genero
            specs = specs.and(LivroSpecs.generoEqual(genero));
        }

        Pageable pageRequest = PageRequest.of(pagina, tamPagina);

        return repository.findAll(specs, pageRequest);
    }

    public void atualizar(Livro livro) {
        if (livro.getId() == null) {
            throw new IllegalArgumentException("O livro deve existir para ser atualizado!");
        }
        validator.validar(livro);
        repository.save(livro);
    }

    public List<Livro> findAll() {
        return repository.findAll();
    }
}
