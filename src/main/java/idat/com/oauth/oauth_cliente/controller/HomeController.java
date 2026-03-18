package idat.com.oauth.oauth_cliente.controller;

import idat.com.oauth.oauth_cliente.dto.RegistroUsuarioRequest;
import idat.com.oauth.oauth_cliente.dto.UsuarioDTO;
import idat.com.oauth.oauth_cliente.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class HomeController {

    private final UsuarioService usuarioService;

    @GetMapping("/")
    public String home() {
        return "API OAuth2 + Login Local operativa. Usa /api/login para validar sesión.";
    }

    @PostMapping("/api/auth/register")
    public UsuarioDTO registrar(@Valid @RequestBody RegistroUsuarioRequest request) {
        return usuarioService.registrarUsuarioLocal(request);
    }

    @GetMapping("/api/login")
    public String login() {
        return "Login correcto. Si es OAuth2 GitHub, inicia en /oauth2/authorization/github";
    }

    @GetMapping("/api/usuario/perfil")
    public UsuarioDTO perfil() {
        return usuarioService.obtenerPerfilAutenticado();
    }

    @GetMapping("/api/usuario/lista")
    public List<UsuarioDTO> lista() {
        return usuarioService.listarUsuarios();
    }
}
