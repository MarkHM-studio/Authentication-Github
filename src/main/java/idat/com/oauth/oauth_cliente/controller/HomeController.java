package idat.com.oauth.oauth_cliente.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class HomeController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/hello")
    public String home() {
        return "Hello World! - Endpoint público";
    }

    @GetMapping("/hellosecured")
    public String hellosecured(@AuthenticationPrincipal OAuth2User principal) {
        String nombre = principal.getAttribute("login");
        return "Hola " + nombre + "! - Bienvenido desde GitHub";
    }

    @GetMapping("/repos")
    public List<Map<String, Object>> repos(
            @RegisteredOAuth2AuthorizedClient("github") OAuth2AuthorizedClient client) {

        String token = client.getAccessToken().getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        ResponseEntity<Map[]> response = restTemplate.exchange(
                "https://api.github.com/user/repos?per_page=100&sort=updated",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map[].class
        );

        return Arrays.stream(response.getBody())
                .map(repo -> Map.of(
                        "name", repo.get("name"),
                        "description", repo.get("description") != null ? repo.get("description") : "",
                        "html_url", repo.get("html_url"),
                        "language", repo.get("language") != null ? repo.get("language") : "",
                        "private", repo.get("private")
                ))
                .toList();
    }
}
