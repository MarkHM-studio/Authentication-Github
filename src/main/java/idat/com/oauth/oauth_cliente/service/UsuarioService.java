package idat.com.oauth.oauth_cliente.service;

import idat.com.oauth.oauth_cliente.dto.RegistroUsuarioRequest;
import idat.com.oauth.oauth_cliente.dto.UsuarioDTO;
import idat.com.oauth.oauth_cliente.entity.Usuario;
import java.util.List;

public interface UsuarioService {

    UsuarioDTO registrarUsuarioLocal(RegistroUsuarioRequest request);

    UsuarioDTO obtenerPerfilAutenticado();

    List<UsuarioDTO> listarUsuarios();

    Usuario obtenerEntidadAutenticada();

    Usuario upsertGithubUser(String githubId, String username, String email, String avatarUrl);

    void actualizarTokenGithub(String githubId, String accessToken);
}
