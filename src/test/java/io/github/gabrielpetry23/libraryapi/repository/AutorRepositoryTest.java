package io.github.gabrielpetry23.libraryapi.repository;

import io.github.gabrielpetry23.libraryapi.model.Autor;
import io.github.gabrielpetry23.libraryapi.model.GeneroLivro;
import io.github.gabrielpetry23.libraryapi.model.Livro;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class AutorRepositoryTest {

    @Autowired
    AutorRepository repository;

    @Autowired
    LivroRepository livroRepository;

    @Test
    public void salvarTest() {
        Autor autor = new Autor();
        autor.setNome("Maria");
        autor.setNacionalidade("Brasileiro");
        autor.setDataNascimento(LocalDate.of(2005, 10, 30));

        var autorSalvo = repository.save(autor);
        System.out.print(autorSalvo);
    }

    @Test
    public void atualizarTest() {
        var id = UUID.fromString("ec97b44d-2f7c-467f-8de6-57fc11139189");

        Optional<Autor> possivelAutor = repository.findById(id);

        if (possivelAutor.isPresent()) {
            Autor autorEncontrado = possivelAutor.get();
            System.out.println("Dados do autor:");
            System.out.println(autorEncontrado);

            autorEncontrado.setDataNascimento(LocalDate.of(2000,1,23));

            repository.save(autorEncontrado);
        }
    }

    @Test
    public void listarTest() {
        List<Autor> autores = repository.findAll();
        autores.forEach(System.out::println);
    }

    @Test
    public void countTest() {
        System.out.println("Contagem de autores:" +repository.count());
    }

    @Test
    public void deleteByIdTest() {
        var id = UUID.fromString("ec97b44d-2f7c-467f-8de6-57fc11139189");
        repository.deleteById(id);
    }

    @Test
    public void deleteTest() {
        var id = UUID.fromString("ec97b44d-2f7c-467f-8de6-57fc11139189");
        var autor = repository.findById(id).get();
        repository.delete(autor);
    }

    @Test
    void salvarAutorComLivros() {

        Autor autor = new Autor();
        autor.setNome("Joaooo");
        autor.setNacionalidade("Brasileiro");
        autor.setDataNascimento(LocalDate.of(2006, 10, 30));

        Livro livro1 = new Livro();
        livro1.setPreco(BigDecimal.valueOf(100));
        livro1.setGenero(GeneroLivro.FICCAO);
        livro1.setTitulo("ufoooo");
        livro1.setDataPublicacao(LocalDate.of(1980, 1, 2));
        livro1.setAutor(autor);

        Livro livro2 = new Livro();
        livro2.setPreco(BigDecimal.valueOf(100));
        livro2.setGenero(GeneroLivro.FICCAO);
        livro2.setTitulo("ufoooo");
        livro2.setDataPublicacao(LocalDate.of(1980, 1, 2));
        livro2.setAutor(autor);

        autor.setLivros(new ArrayList<>());
        autor.getLivros().add(livro1);
        autor.getLivros().add(livro2);

        repository.save(autor);
        livroRepository.saveAll(autor.getLivros());
    }

    @Test
    void listarLivrosAutor(Autor autor) {
        List<Livro> lista = livroRepository.findByAutor(autor);
        autor.setLivros(lista);
        autor.getLivros().forEach(System.out::println);
    }
}
