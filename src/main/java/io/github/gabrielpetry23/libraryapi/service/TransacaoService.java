package io.github.gabrielpetry23.libraryapi.service;

import io.github.gabrielpetry23.libraryapi.model.Autor;
import io.github.gabrielpetry23.libraryapi.model.GeneroLivro;
import io.github.gabrielpetry23.libraryapi.model.Livro;
import io.github.gabrielpetry23.libraryapi.repository.AutorRepository;
import io.github.gabrielpetry23.libraryapi.repository.LivroRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class TransacaoService {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Transactional
    public void executarTransacao() {
        // Salvar autor
        Autor autor = new Autor();
        autor.setNome("Joao");
        autor.setNacionalidade("Brasileiro");
        autor.setDataNascimento(LocalDate.of(2003, 10, 30));
        autorRepository.save(autor);

        // Salvar livro
        Livro livro = new Livro();
        livro.setPreco(BigDecimal.valueOf(100));
        livro.setGenero(GeneroLivro.FICCAO);
        livro.setTitulo("UFO");
        livro.setDataPublicacao(LocalDate.of(1980, 1, 2));
        livro.setAutor(autor);
        livroRepository.save(livro);

        // Se a condição não passar vai dar Rollback, ou seja
        // As alterações/operações feitas até este momento não serão salvas
        // e a transação chegará ao seu fim

        // alterações/operações feitas até este momento só serão salvas se:
        // for dado um commit anteriormente, no caso se utilizasse saveAndFlush()
        // autorRepository.saveAndFlush(autor); teria salvado o autor mesmo com rollback

        if (autor.getNome().equals("José")) {
            throw new RuntimeException("Rollback");
        }

        // E caso transação der certo ele irá comitar automaticamente
        // sem necessidade de dar um .save
        // (Maior parte dos casos)

        // Alugar o livro
        // Enviar email pro locatario
        // Notificar que o livro saiu da livraria
    }
 }
