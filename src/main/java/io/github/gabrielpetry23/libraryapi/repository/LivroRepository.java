package io.github.gabrielpetry23.libraryapi.repository;

import io.github.gabrielpetry23.libraryapi.model.Autor;
import io.github.gabrielpetry23.libraryapi.model.GeneroLivro;
import io.github.gabrielpetry23.libraryapi.model.Livro;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LivroRepository extends JpaRepository<Livro, UUID>, JpaSpecificationExecutor<Livro> {

    //QUERY METHODS

    //select * from livro where titulo = t
    List<Livro> findByTitulo(String t);

    //select * from livro where isbn = s
    Optional<Livro> findByIsbn(String s);

    //select * from livro where l.autor = autor
    List<Livro> findByAutor(Autor autor);

    //select * from livro where titulo = t and preco = p
    List<Livro> findByTituloAndPreco(String t, BigDecimal p);

    // A SINTAXE TEM QUE ESTAR CORRETA !
    // List<Livro> findByautor(Autor autor); NÃO FUNCIONA
    // List<Livro> findbyTituloandPreco(String t, BigDecimal p); NÃO FUNCIONA

    //Criar Query / JPQL
    //deve utilizar as columns como definiu na classe Livro (ex: titulo, preco)
    //select l.* from livro as l order by l.titulo, l.preco

    @Query(" SELECT l FROM Livro l ORDER BY l.titulo, l.preco ")
    List<Livro> listarOrdenadosPorTituloPreco();

    //SELECT a.* FROM livro l JOIN autor a ON a.id = l.id_autor
    @Query(" SELECT a FROM Livro l JOIN l.autor a ")
    List<Autor> listarAutoresLivro();

    //SELECT DISTINCT l.* FROM livro l
    @Query(" SELECT DISTINCT l.titulo FROM Livro l ")
    List<String> listarNomesDiferentesLivro();

    @Query("""
            SELECT l.genero
            FROM Livro l
            JOIN l.autor a
            WHERE a.nacionalidade = 'Brasileiro'
            ORDER BY l.genero
            """)
    List<String> listarGenerosAutoresBrasileiros();

    @Query(" SELECT l FROM Livro l where l.genero = :generoParametro order by :paramOrdenacao ")
    List<Livro> listarPeloGenero( @Param("generoParametro") GeneroLivro generoLivro, @Param("paramOrdenacao") String nomePropiedade);

    // ou pode-se utilizar positional parameters (evita uso do @Param)

    @Query(" SELECT l FROM Livro l where l.genero = ?1 order by ?2")
    List<Livro> listarPeloGenero2(GeneroLivro generoLivro, String nomePropiedade);

    @Modifying // Para operações de escrita (para consultas não é necessário)
    @Transactional // Transforma método em uma transação de escrita
    @Query(" DELETE FROM Livro where genero = ?1 ")
    void deleteByGenero(GeneroLivro genero);

    @Modifying
    @Query(" UPDATE Livro SET dataPublicacao = ?1 ")
    void updateDataPublicacao(LocalDate data);

    boolean existsByAutor(Autor autor);


}
