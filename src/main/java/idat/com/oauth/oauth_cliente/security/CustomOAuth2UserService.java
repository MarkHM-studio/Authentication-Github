package idat.com.oauth.oauth_cliente.security;

import idat.com.oauth.oauth_cliente.service.UsuarioService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UsuarioService usuarioService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        Map<String, Object> attributes = oauth2User.getAttributes();

        String githubId = String.valueOf(attributes.get("id"));
        String username = String.valueOf(attributes.get("login"));
        String email = attributes.get("email") != null ? String.valueOf(attributes.get("email")) : null;
        String avatarUrl = attributes.get("avatar_url") != null ? String.valueOf(attributes.get("avatar_url")) : null;

        usuarioService.upsertGithubUser(githubId, username, email, avatarUrl);

        return new DefaultOAuth2User(oauth2User.getAuthorities(), attributes, "login");
    }
}
