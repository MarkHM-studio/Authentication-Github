package idat.com.oauth.oauth_cliente.controller;

import idat.com.oauth.oauth_cliente.dto.RepositorioDTO;
import idat.com.oauth.oauth_cliente.service.GithubApiService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/github")
public class GithubController {

    private final GithubApiService githubApiService;

    @GetMapping("/repositorios")
    public List<RepositorioDTO> repositorios() {
        return githubApiService.obtenerRepositoriosUsuarioAutenticado();
    }

    @GetMapping("/repositorio/{nombre}")
    public RepositorioDTO repositorio(@PathVariable("nombre") String nombre) {
        return githubApiService.obtenerRepositorioPorNombre(nombre);
    }
}
