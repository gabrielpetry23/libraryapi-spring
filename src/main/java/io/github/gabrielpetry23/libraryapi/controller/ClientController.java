package io.github.gabrielpetry23.libraryapi.controller;

import io.github.gabrielpetry23.libraryapi.model.Client;
import io.github.gabrielpetry23.libraryapi.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final ClientService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GERENTE')")
    public void salvar(@RequestBody Client client) {
        log.info("Salvando cliente: {} com scope: {}", client.getClientId(), client.getScope());
        service.salvar(client);
    }
}
