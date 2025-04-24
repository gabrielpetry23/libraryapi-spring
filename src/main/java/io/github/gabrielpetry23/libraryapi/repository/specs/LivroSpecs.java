package io.github.gabrielpetry23.libraryapi.repository.specs;

import io.github.gabrielpetry23.libraryapi.model.GeneroLivro;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import io.github.gabrielpetry23.libraryapi.model.Livro;

public class LivroSpecs {

    // select * from livro

    public static Specification<Livro> isbnEqual(String isbn) {
        // where isbn = :isbn
        return (root, query, cb)
                -> cb.equal(root.get("isbn"), isbn);
    }

    public static Specification<Livro> tituloLike(String titulo) {
        // where upper(livro.titulo) like upper(:%titulo%)
        // % serve para informar que a pesquisa pode ter qualquer coisa antes (%ex) ou depois (ex%) do que foi digitado
        return (root, query, cb)
                -> cb.like(cb.upper(root.get("titulo")), "%" +titulo.toUpperCase() + "%");
    }

    public static Specification<Livro> generoEqual(GeneroLivro genero) {
        // where livro.genero = :genero
        return (root, query, cb)
                -> cb.equal(root.get("genero"), genero);
    }

    /*
    public static Specification<Livro> anoPublicacaoEqual(Integer anoPublicacao) {
        // where year(livro.dataPublicacao) = :anoPublicacao // no postgresql não existe YEAR
        return (root, query, cb)
                -> cb.equal(cb.function("YEAR", Integer.class, root.get("dataPublicacao")), anoPublicacao);
    }
     */

    public static Specification<Livro> anoPublicacaoEqual(Integer anoPublicacao) {
        // where year(livro.dataPublicacao) = :anoPublicacao // no postgresql não existe YEAR
        // então seria:
        // and to_char(livro.dataPublicacao, 'YYYY') = :anoPublicacao
        return (root, query, cb)
                -> cb.equal( cb.function("to_char", String.class, root.get("dataPublicacao"),
                cb.literal("YYYY")), anoPublicacao.toString());
    }

    public static Specification<Livro> autorNomeLike(String nomeAutor) {
        //  select l.* from livro as l join autor as a on a.id = l.id_autor where upper(a.nome) like upper(:%nomeAutor%)
        //return (root, query, cb)
        //        -> cb.like(cb.upper(root.join("autor").get("nome")), "%" + nomeAutor.toUpperCase() + "%");
        return (root, query, cb) -> {
            Join<Object, Object> joinAutor = root.join("autor", JoinType.INNER);
            return cb.like(cb.upper(joinAutor.get("nome")), "%" + nomeAutor.toUpperCase() + "%");
        };
    }
}
