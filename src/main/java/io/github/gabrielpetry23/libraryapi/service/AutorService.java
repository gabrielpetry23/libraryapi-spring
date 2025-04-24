package io.github.gabrielpetry23.libraryapi.service;

import io.github.gabrielpetry23.libraryapi.exceptions.OperacaoNaoPermitidaException;
import io.github.gabrielpetry23.libraryapi.model.Autor;
import io.github.gabrielpetry23.libraryapi.model.Usuario;
import io.github.gabrielpetry23.libraryapi.repository.AutorRepository;
import io.github.gabrielpetry23.libraryapi.repository.LivroRepository;
import io.github.gabrielpetry23.libraryapi.validators.AutorValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import io.github.gabrielpetry23.libraryapi.security.SecurityService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor // cria o construtor automaticamente com os campos obrigatórios (final)
public class AutorService {

    private final AutorRepository repository;
    private final AutorValidator validator;
    private final LivroRepository livroRepository;
    private final SecurityService securityService;

    public Autor salvar(Autor autor) {
        validator.validar(autor);
        Usuario currentUser = securityService.getCurrentUser();

        autor.setUsuario(currentUser);
        // agora se buscarmos no banco temos acesso a quem realizou a operação
        return repository.save(autor);
    }

    public void atualizar(Autor autor) {
        if (autor.getId() == null) {
            throw new IllegalArgumentException("Esse autor não existe");
        }
        validator.validar(autor);
        repository.save(autor);
    }

    public List<Autor> listar() {
        return repository.findAll();
    }

    public List<Autor> listarPeloNome(String nome) {
        if (nome != null) {
            return repository.findByNome(nome);
        }
        return repository.findAll();
    }

    public Optional<Autor> findById(UUID id) {
        return repository.findById(id);
    }

    public void deletarById(UUID idAutor) {
        repository.deleteById(idAutor);
    }

    public void deletar(Autor autor) {
        if (possuiLivro(autor)) {
            throw new OperacaoNaoPermitidaException("Esse autor não pode ser removido, pois possui livros associados.");
        }
        repository.delete(autor);
    }

    private boolean possuiLivro(Autor autor) {
        return livroRepository.existsByAutor(autor);
    }

    public List<Autor> pesquisa(String nome, String nacionalidade) {
        if (nome != null && nacionalidade != null) {
            return repository.findByNomeAndNacionalidade(nome, nacionalidade);
        }

        if (nome != null) {
            return repository.findByNome(nome);
        }

        if (nacionalidade != null) {
            return repository.findByNacionalidade(nacionalidade);
        }

        return repository.findAll();
    }

    public List<Autor> pesquisaByExample(String nome, String nacionalidade) {
        var autor = new Autor();
        autor.setNome(nome);
        autor.setNacionalidade(nacionalidade);

        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnorePaths("id")
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Autor> autorExample = Example.of(autor, matcher);
        return repository.findAll(autorExample);
    }

    public void deletarTodos() {
        if (livroRepository.count() > 0) {
            throw new OperacaoNaoPermitidaException("Não é possível deletar todos os autores, pois existem livros associados.");
        }
        repository.deleteAll();
    }

    public void deletarTodosAutoresQueNaoTemLivro() {
        List<Autor> autoresSemLivros = repository.findAll().stream()
                .filter(autor -> !livroRepository.existsByAutor(autor))
                .toList();
        repository.deleteAll(autoresSemLivros);
    }
}
