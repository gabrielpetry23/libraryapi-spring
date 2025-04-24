package io.github.gabrielpetry23.libraryapi.security;

import io.github.gabrielpetry23.libraryapi.model.Usuario;
import io.github.gabrielpetry23.libraryapi.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtCustomAuthenticationFilter extends OncePerRequestFilter {

    // Classe responsável por interceptar as requisições, verificar se o token JWT é válido e por converter o JwtAuthenticationToken para o CustomAuthentication

    private final UsuarioService usuarioService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Só irá fazer a customização se for do OAuth2, pois as outras já estão configuradas/customizadas

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(deveConverter(authentication)) {
            String login = authentication.getName();
            Usuario usuario = usuarioService.obterPorLogin(login);
            if (usuario != null) {
                authentication = new CustomAuthentication(usuario);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean deveConverter(Authentication authentication) {
        // Verifica se a autenticação é do tipo JwtAuthenticationToken pq se for vamos converter para CustomAuthentication
        return authentication != null && authentication instanceof JwtAuthenticationToken;
    }
}
