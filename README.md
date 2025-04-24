# Library API: API RESTful para Gerenciamento de Biblioteca

## Visão Geral

Projeto de estudo abrangendo o desenvolvimento de uma API RESTful com Spring Boot, explorando a implementação de segurança através do Spring Security com OAuth2 e tokens JWT, além do uso do Spring Data JPA para persistência de dados. Este projeto também demonstra a criação e o gerenciamento de recursos da API, como autores e livros, com validações e tratamento de erros centralizado.

## Tecnologias Utilizadas

* **Spring Boot:** Framework para desenvolvimento rápido de aplicações Java.
* **Spring Security:** Framework para segurança de aplicações Spring.
* **OAuth2:** Protocolo de autorização para acesso delegado.
* **JWT (JSON Web Tokens):** Padrão para criação de tokens de segurança.
* **Spring Data JPA:** Para persistência de dados utilizando JPA e Hibernate.
* **PostgreSQL:** Banco de dados relacional utilizado para persistir os dados da aplicação.
* **HikariCP:** Pool de conexões JDBC de alta performance.
* **Lombok:** Para reduzir o boilerplate de código Java.
* **Swagger/OpenAPI:** Para documentação da API (endpoints e modelos).
* **Log4j:** Para logging da aplicação.

## Funcionalidades Principais

A API oferece os seguintes endpoints:

* **`/autores`:**
    * `POST`: Salvar um novo autor (requer role `GERENTE`). Validação de unicidade de autor.
    * `GET /{id}`: Obter detalhes de um autor pelo ID.
    * `DELETE /{id}`: Deletar um autor pelo ID.
    * `GET /pesquisar`: Pesquisar autores por nome e/ou nacionalidade.
    * `GET /all`: Listar todos os autores.
    * `PUT /{id}`: Atualizar informações de um autor pelo ID. Validação de unicidade de autor na atualização.
    * `GET /nome`: Buscar autores pelo nome.
    * `DELETE /all`: Deletar todos os autores (requer tratamento de `OperacaoNaoPermitidaException`).
    * `DELETE /allWithNoBooks`: Deletar todos os autores que não possuem livros associados.
* **`/clients`:**
    * `POST`: Salvar um novo client OAuth2 (requer role `GERENTE`).
* **`/livros`:**
    * `POST`: Salvar um novo livro (requer role `OPERADOR` ou `GERENTE`).
    * `GET /{id}`: Obter detalhes de um livro pelo ID (requer role `OPERADOR` ou `GERENTE`).
    * `DELETE /{id}`: Deletar um livro pelo ID (requer role `OPERADOR` ou `GERENTE`).
    * `GET /pesquisar`: Pesquisar livros com suporte a paginação (requer role `OPERADOR` ou `GERENTE`).
    * `PUT /{id}`: Atualizar informações de um livro pelo ID (requer role `OPERADOR` ou `GERENTE`).
    * `GET /all-titulos`: Listar todos os títulos dos livros.
* **`/usuarios`:**
    * `POST`: Salvar um novo usuário (endpoint público).
* **`/login`:**
    * Endpoint para login (formulário padrão do Spring Security). Redireciona para `login.html`.
* **`/oauth2/token`:**
    * Endpoint do servidor de autorização para obter tokens OAuth2.
* **`/oauth2/authorize`:**
    * Endpoint do servidor de autorização para autorização (fluxo Authorization Code).
* **`/oauth2/jwks`:**
    * Endpoint para expor as chaves públicas JWT para verificação.
* **`/` (Home):**
    * Endpoint simples que retorna uma mensagem de boas-vindas com o nome do usuário autenticado.
* **`/authorized`:**
    * Endpoint para receber o código de autorização no fluxo OAuth2 Authorization Code.

## Conceitos de Segurança Implementados

* **Autenticação OAuth2:** A API utiliza o protocolo OAuth2 para autenticação e autorização, suportando o fluxo Authorization Code e Client Credentials.
* **Tokens JWT (JSON Web Tokens):** Os tokens de acesso são JWTs, contendo informações sobre o usuário e suas autoridades.
* **Servidor de Autorização:** O projeto configura um servidor de autorização (`AuthorizationServerConfiguration`) responsável por emitir tokens JWT após a autenticação de usuários e clients.
* **Servidor de Recursos:** A API atua como um servidor de recursos (`SecurityConfiguration`), protegendo seus endpoints e validando os tokens JWT recebidos.
* **Controle de Acesso Baseado em Roles:** Acesso a diferentes endpoints é controlado por roles (`GERENTE`, `OPERADOR`, `USER`). As anotações `@PreAuthorize` e `@Secured` são utilizadas para proteger os métodos dos controllers.
* **Geração de Chaves RSA:** O servidor de autorização gera um par de chaves RSA para assinar os tokens JWT (`JWKSource`).
* **Customização do Token JWT:** O `tokenCustomizer` adiciona informações personalizadas (autoridades e email do usuário) ao token de acesso.
* **Proteção de Rotas:** O `SecurityFilterChain` no `SecurityConfiguration` define quais rotas são públicas e quais exigem autenticação.
* **Tratamento de Erros de Segurança:** O `GlobalExceptionHandler` centraliza o tratamento de diversas exceções, incluindo `AccessDeniedException` (acesso negado), garantindo respostas de erro padronizadas para o cliente.
* **Autenticação com Formulário:** Suporte para autenticação via formulário de login padrão do Spring Security (`/login`).
* **Login Social:** Implementação de login social (ex: Google) através do Spring Security OAuth2 Client. Novos usuários são cadastrados automaticamente.
* **Custom Authentication Provider:** Um `CustomAuthenticationProvider` é utilizado para autenticar usuários com base em suas credenciais (login e senha) armazenadas no banco de dados.
* **Custom UserDetailsService:** Um `CustomUserDetailsService` carrega os detalhes do usuário do banco de dados para a autenticação baseada em nome de usuário e senha.
* **JwtCustomAuthenticationFilter:** Um filtro personalizado intercepta requisições com tokens JWT e converte o `JwtAuthenticationToken` padrão para um `CustomAuthentication` contendo a entidade `Usuario` completa.
* **Custom RegisteredClientRepository:** Um repositório personalizado (`CustomRegisteredClientRepository`) busca os clients OAuth2 do banco de dados.
* **SecurityService:** Um serviço utilitário para obter o usuário autenticado no contexto de segurança.

## Validações e Tratamento de Erros

* **Validação de Dados:** A API utiliza anotações de validação (`@NotNull`, `@NotBlank`, `@Size`, `@Email`, `@Past`, etc.) nos DTOs para garantir a integridade dos dados recebidos.
* **Validação de Unicidade:** Implementações de validação (como `AutorValidator` e a lógica no serviço de Livros) garantem a unicidade de registros como autores e ISBNs de livros.
* **Tratamento Centralizado de Exceções:** O `GlobalExceptionHandler` intercepta e trata diversas exceções lançadas pela aplicação, como:
    * `MethodArgumentNotValidException`: Erros de validação nos dados de entrada.
    * `RegistroDuplicadoException`: Tentativa de criar registros duplicados.
    * `OperacaoNaoPermitidaException`: Operações que não podem ser realizadas devido a regras de negócio.
    * `CampoInvalidoException`: Erros específicos em campos.
    * `AccessDeniedException`: Tentativa de acessar recursos sem a autorização necessária.
    * `RuntimeException`: Erros inesperados.
    As respostas de erro são formatadas de maneira consistente para o cliente.

## Como Executar o Projeto

1.  **Pré-requisitos:**
    * Java Development Kit (JDK) instalado.
    * Maven instalado.
    * PostgreSQL instalado e configurado (verifique as configurações de conexão em `src/main/resources/application.properties` ou `application.yml`).

2.  **Configurar o Banco de Dados:**
    * Crie um banco de dados PostgreSQL com o nome especificado nas configurações (`spring.datasource.url`).
    * Verifique as credenciais de acesso ao banco de dados (`spring.datasource.username` e `spring.datasource.password`) e ajuste-as se necessário.
    * O Spring Data JPA cuidará da criação do esquema do banco de dados com base nas suas entidades.

3.  **Clonar o repositório:**
    ```bash
    git clone [https://github.com/gabrielpetry23/library-api](https://github.com/gabrielpetry23/library-api)
    ```

4.  **Navegar para o diretório do projeto:**
    ```bash
    cd library-api
    ```

5.  **Executar a aplicação Spring Boot:**
    ```bash
    mvn spring-boot:run
    ```

    A aplicação estará disponível em `http://localhost:8080`.

6.  **(Opcional) Acessar a documentação Swagger:**
    A documentação da API pode ser acessada em `http://localhost:8080/swagger-ui/index.html`.

7.  **(Opcional) Acessar a página de login:**
    A página de login padrão do Spring Security está disponível em `http://localhost:8080/login`.

## Aprendizados

Durante o desenvolvimento deste projeto, foram adquiridos conhecimentos sobre:

* A implementação completa do fluxo de autenticação e autorização com OAuth2, incluindo a configuração de um servidor de autorização e um servidor de recursos.
* A geração, assinatura e validação de tokens JWT para proteger a comunicação entre o cliente e a API.
* A integração do Spring Security com OAuth2 e JWT para proteger diferentes tipos de recursos e endpoints.
* A implementação de controle de acesso baseado em roles e a utilização de anotações para proteger métodos.
* A configuração de um servidor de autorização utilizando Spring Authorization Server, incluindo a definição de clients e configurações de token.
* A customização do processo de autenticação com `AuthenticationProvider` e `UserDetailsService`.
* A manipulação de autenticação social com Spring Security OAuth2 Client.
* O uso de filtros personalizados (`JwtCustomAuthenticationFilter`) para integrar a autenticação JWT com a lógica da aplicação.
* A persistência de dados utilizando Spring Data JPA e a configuração de um banco de dados PostgreSQL.
* O uso do HikariCP para gerenciamento eficiente de conexões com o banco de dados.
* A documentação da API utilizando Swagger/OpenAPI.
* O tratamento centralizado de diversas exceções com `@RestControllerAdvice` para fornecer respostas de erro consistentes e padronizadas.
* A implementação de validação de dados nos DTOs para garantir a integridade das requisições.
* A implementação de lógica de validação para garantir a unicidade de registros de autores e livros.


## Considerações Finais

Este projeto foi desenvolvido como um estudo abrangente sobre a segurança de APIs RESTful com Spring Security e OAuth2, explorando a integração com um banco de dados PostgreSQL e abordando diversos aspectos da autenticação, autorização, validação de dados e tratamento de erros centralizado. Ele demonstra a implementação de conceitos importantes para a construção de APIs seguras, robustas e bem estruturadas.
