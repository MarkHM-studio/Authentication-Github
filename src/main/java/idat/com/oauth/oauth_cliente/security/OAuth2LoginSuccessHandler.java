package idat.com.oauth.oauth_cliente.security;

import idat.com.oauth.oauth_cliente.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final UsuarioService usuarioService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName());

        if (authorizedClient != null) {
            OAuth2User user = oauthToken.getPrincipal();
            String githubId = String.valueOf(user.getAttributes().get("id"));
            /*String githubId = String.valueOf(Objects.requireNonNull(oauthToken.getPrincipal().getAttribute("id")));
            */
            String token = authorizedClient.getAccessToken().getTokenValue();
            usuarioService.actualizarTokenGithub(githubId, token);
            System.out.println("GitHub ID: " + githubId);
            System.out.println("TOKEN: " + token);
        }

        response.sendRedirect("/api/usuario/perfil");
    }
}

