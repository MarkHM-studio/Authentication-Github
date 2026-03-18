package idat.com.oauth.oauth_cliente.service.impl;

import idat.com.oauth.oauth_cliente.dto.RepositorioDTO;
import idat.com.oauth.oauth_cliente.entity.Usuario;
import idat.com.oauth.oauth_cliente.enumtype.AuthProvider;
import idat.com.oauth.oauth_cliente.exception.BadRequestException;
import idat.com.oauth.oauth_cliente.exception.GithubIntegrationException;
import idat.com.oauth.oauth_cliente.exception.ResourceNotFoundException;
import idat.com.oauth.oauth_cliente.service.GithubApiService;
import idat.com.oauth.oauth_cliente.service.UsuarioService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GithubApiServiceImpl implements GithubApiService {

    private final WebClient.Builder webClientBuilder;
    private final UsuarioService usuarioService;

    @Override
    public List<RepositorioDTO> obtenerRepositoriosUsuarioAutenticado() {
        Usuario usuario = usuarioGithubAutenticado();

        List<Map<String, Object>> repos = webClientBuilder.baseUrl("https://api.github.com")
                .build()
                .get()
                .uri("/user/repos?per_page=100&sort=updated")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + usuario.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                .collectList()
                .onErrorResume(ex -> Mono.error(new GithubIntegrationException("Error al consumir API de GitHub: " + ex.getMessage())))
                .block();

        if (repos == null) {
            return List.of();
        }

        return repos.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public RepositorioDTO obtenerRepositorioPorNombre(String nombreRepositorio) {
        Usuario usuario = usuarioGithubAutenticado();

        Map<String, Object> repo = webClientBuilder.baseUrl("https://api.github.com")
                .build()
                .get()
                .uri("/repos/{owner}/{repo}", usuario.getUsername(), nombreRepositorio)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + usuario.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.error(new ResourceNotFoundException("Repositorio no encontrado en GitHub")))
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .onErrorResume(ResourceNotFoundException.class, Mono::error)
                .onErrorResume(ex -> Mono.error(new GithubIntegrationException("Error al consultar repositorio: " + ex.getMessage())))
                .block();

        if (repo == null) {
            throw new ResourceNotFoundException("Repositorio no encontrado en GitHub");
        }

        return mapToDto(repo);
    }

    private Usuario usuarioGithubAutenticado() {
        Usuario usuario = usuarioService.obtenerEntidadAutenticada();
        if (usuario.getAuthProvider() != AuthProvider.GITHUB) {
            throw new BadRequestException("El usuario autenticado no inició sesión con GitHub");
        }
        if (usuario.getAccessToken() == null || usuario.getAccessToken().isBlank()) {
            throw new BadRequestException("No hay token de acceso GitHub almacenado para este usuario");
        }
        return usuario;
    }

    private RepositorioDTO mapToDto(Map<String, Object> repo) {
        return new RepositorioDTO(
                String.valueOf(repo.get("name")),
                repo.get("description") != null ? String.valueOf(repo.get("description")) : "",
                repo.get("html_url") != null ? String.valueOf(repo.get("html_url")) : "",
                repo.get("language") != null ? String.valueOf(repo.get("language")) : ""
        );
    }
}
