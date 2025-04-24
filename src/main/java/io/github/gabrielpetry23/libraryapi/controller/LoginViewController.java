package io.github.gabrielpetry23.libraryapi.controller;

import io.github.gabrielpetry23.libraryapi.security.CustomAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginViewController {

    @GetMapping("/login")
    public String paginaLogin() {
        return "login"; // Nome do arquivo HTML registrado na ViewControllerRegistry da classe WebConfiguration
    }

    @GetMapping("/")
    @ResponseBody
    public String paginaHome(Authentication authentication) {

        if (authentication instanceof CustomAuthentication customAuth) {
            System.out.println(customAuth.getUsuario());
        }
        return "Olá " + authentication.getName() + ", você está logado!";
    }

    @GetMapping("/authorized")
    @ResponseBody
    public String getAuthorizationCode(@RequestParam("code") String code) {
        return "Authorization code: " + code;
    }
}
