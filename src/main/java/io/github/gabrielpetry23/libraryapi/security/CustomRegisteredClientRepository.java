package io.github.gabrielpetry23.libraryapi.security;

import io.github.gabrielpetry23.libraryapi.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;

// Não preciso registrar esse repositório em outro ligar como o SecurityFilterChain, pois o Spring Security já faz isso automaticamente, já que ele é um componente (foi anotado como @Component)  do Spring e esta implementando a interface RegisteredClientRepository.

@Component
@RequiredArgsConstructor
public class CustomRegisteredClientRepository implements RegisteredClientRepository {

    private final ClientService clientService;
    private final TokenSettings tokenSettings;
    private final ClientSettings clientSettings;

    @Override
    public void save(RegisteredClient registeredClient) {
        // Não é necessário implementar o método save, pois não estamos salvando os clientes registrados em um banco de dados.
        // O cliente registrado é obtido diretamente do banco de dados quando necessário.
        // Portanto, podemos deixar esse método vazio ou lançar uma exceção se desejarmos impedir a gravação.
        throw new UnsupportedOperationException("Não é possível salvar clientes registrados.");
    }

    @Override
    public RegisteredClient findById(String id) {
        return null;
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        var client = clientService.obterPorClientID(clientId);

        if (client == null) {
            return null;
        }

        return RegisteredClient.withId(client.getId().toString())
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .scope(client.getScope())
                .redirectUri(client.getRedirectURI())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                //.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenSettings(tokenSettings)
                //.tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(60)).build())
                .clientSettings(clientSettings)
                .build();
    }
}
