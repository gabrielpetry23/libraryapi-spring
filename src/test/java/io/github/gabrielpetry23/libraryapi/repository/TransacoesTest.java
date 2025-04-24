package io.github.gabrielpetry23.libraryapi.repository;

import io.github.gabrielpetry23.libraryapi.service.TransacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TransacoesTest {

    @Autowired
    TransacaoService service;

    // Commit - salvar alterações
    // Rollback - desfazer alterações

    @Test
    void transacaoSimples() {
        service.executarTransacao();
        // salvar um livro
        // salvar autor do livro
        // alugar o livro
        // enviar email pro locatario
        // notificar que o livro saiu da livraria

    }

}
