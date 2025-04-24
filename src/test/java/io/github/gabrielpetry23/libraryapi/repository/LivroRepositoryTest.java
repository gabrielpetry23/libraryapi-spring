package io.github.gabrielpetry23.libraryapi.repository;

import io.github.gabrielpetry23.libraryapi.model.Autor;
import io.github.gabrielpetry23.libraryapi.model.GeneroLivro;
import io.github.gabrielpetry23.libraryapi.model.Livro;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LivroRepositoryTest {

    @Autowired
    LivroRepository repository;

    @Autowired
    AutorRepository autorRepository;

    @Test
    void salvarTest() {
        Livro livro = new Livro();
        livro.setPreco(BigDecimal.valueOf(100));
        livro.setGenero(GeneroLivro.FICCAO);
        livro.setTitulo("UFO");
        livro.setDataPublicacao(LocalDate.of(1980, 1, 2));
        livro.setIsbn("1234-5678");

        Autor autor = autorRepository
                .findById(UUID.fromString("b01ddca8-335e-4f68-a967-cf7a4538381b"))
                .orElse(null);

        livro.setAutor(autor);

        repository.save(livro);
    }

    void salvarCascadeTest() {
        Livro livro = new Livro();
        livro.setPreco(BigDecimal.valueOf(100));
        livro.setGenero(GeneroLivro.FICCAO);
        livro.setTitulo("UFO");
        livro.setDataPublicacao(LocalDate.of(1980, 1, 2));

        Autor autor = new Autor();
        autor.setNome("Joao");
        autor.setNacionalidade("Brasileiro");
        autor.setDataNascimento(LocalDate.of(2003, 10, 30));

        //autorRepository.save(autor);

        livro.setAutor(autor);

        repository.save(livro);
    }

    @Test
    void atualizarAutorDoLivro(){
        UUID id = UUID.fromString("cfbc87ce-5932-4792-bff0-78ef5973861b");
        var livroParaAtualizar = repository.findById(id).orElse(null);

        UUID idAutor = UUID.fromString("76e7c418-ccf9-4e2a-af20-c28b9e50ab55");
        Autor maria = autorRepository.findById(idAutor).orElse(null);

        livroParaAtualizar.setAutor(maria);

        repository.save(livroParaAtualizar);
    }

    @Test
    void deletar(){
        UUID id = UUID.fromString("cfbc87ce-5932-4792-bff0-78ef5973861b");
        repository.deleteById(id);
    }

    @Test
    void deletarCascade(){
        UUID id = UUID.fromString("22238c02-8118-45ba-a9f0-202dfc3acc67");
        repository.deleteById(id);
        //deleta autor junto
    }

    @Test
    @Transactional
    void buscarLivroTest(){
        UUID id = UUID.fromString("daed83b3-65fd-49eb-9400-cbc0af13059d");
        Livro livro = repository.findById(id).orElse(null);
        System.out.println("Livro:");
        System.out.println(livro.getTitulo());

//        System.out.println("Autor:");
//        System.out.println(livro.getAutor().getNome());
    }

    @Test
    void pesquisaPorTituloTest(){
        List<Livro> lista = repository.findByTitulo("O roubo da casa assombrada");
        lista.forEach(System.out::println);
    }


    @Test
    void pesquisaPorISBNTest(){
        Optional<Livro> livro = repository.findByIsbn("20847-84874");
        livro.ifPresent(System.out::println);
    }


    @Test
    void pesquisaPorTituloEPrecoTest(){
        var preco = BigDecimal.valueOf(204.00);
        var tituloPesquisa = "O roubo da casa assombrada";

        List<Livro> lista = repository.findByTituloAndPreco(tituloPesquisa, preco);
        lista.forEach(System.out::println);
    }

    //QUERYS JPQL

    @Test
    void listarLivrosComQueryJPQL() {
        var result = repository.listarOrdenadosPorTituloPreco();
        result.forEach(System.out::println);
    }

    @Test
    void listarAutoresLivro() {
        var result = repository.listarAutoresLivro();
        result.forEach(System.out::println);
    }

    @Test
    void listarNomesDiferentes() {
        var result = repository.listarNomesDiferentesLivro();
        result.forEach(System.out::println);
    }

    @Test
    void listarGeneroAutoresBrasileiros() {
        var result = repository.listarGenerosAutoresBrasileiros();
        result.forEach(System.out::println);
    }

    @Test
    void listarPeloGenero() {
        var result = repository.listarPeloGenero(GeneroLivro.FICCAO, "preco");
        result.forEach(System.out::println);
    }

    @Test
    void deletarPeloGenero() {
        repository.deleteByGenero(GeneroLivro.BIOGRAFIA);
    }

    @Test
    void atualizarDataPubli() {
        repository.updateDataPublicacao(LocalDate.of(2004,8,23));
    }
}