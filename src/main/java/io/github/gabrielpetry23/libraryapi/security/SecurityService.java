package io.github.gabrielpetry23.libraryapi.security;

import io.github.gabrielpetry23.libraryapi.model.Usuario;
import io.github.gabrielpetry23.libraryapi.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityService {

    // Classe responsável por fornecer informações sobre o usuário autenticado no contexto de segurança do Spring.

    private final UsuarioService usuarioService;

    public Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        String login = userDetails.getUsername();
//        return usuarioService.obterPorLogin(login);
        // Deixei comentado pois podemos utilizar o CustomAuthentication criado
        if (authentication instanceof CustomAuthentication customAuth) {
            return customAuth.getUsuario();
        }

        return null;
    }

}
