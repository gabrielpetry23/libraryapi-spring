package io.github.gabrielpetry23.libraryapi.validators;

import io.github.gabrielpetry23.libraryapi.exceptions.RegistroDuplicadoException;
import io.github.gabrielpetry23.libraryapi.model.Autor;
import io.github.gabrielpetry23.libraryapi.repository.AutorRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

// PARA REGRAS DE NEGÓCIO
@Component
public class AutorValidator {

    private AutorRepository repository;

    public AutorValidator(AutorRepository repository) {
        this.repository = repository;
    }

    public void validar(Autor autor) {
        if (existeAutorCadastrado(autor)) {
            throw new RegistroDuplicadoException("Autor já cadastrado!");
        }
    }

    private boolean existeAutorCadastrado(Autor aProcurado) {
        Optional<Autor> aEncontrado =  repository.findByNomeAndDataNascimentoAndNacionalidade(
                aProcurado.getNome(),
                aProcurado.getDataNascimento(),
                aProcurado.getNacionalidade()
        );

        if (aProcurado.getId() == null) {
            return aEncontrado.isPresent();
        }
        return !aProcurado.getId().equals(aEncontrado.get().getId()) && aEncontrado.isPresent();
    }


}
