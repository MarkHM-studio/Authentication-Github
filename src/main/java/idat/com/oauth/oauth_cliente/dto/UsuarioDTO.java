package idat.com.oauth.oauth_cliente.dto;

import idat.com.oauth.oauth_cliente.enumtype.AuthProvider;
import java.time.LocalDateTime;

public record UsuarioDTO(
        Long id,
        String githubId,
        String username,
        String email,
        String avatarUrl,
        AuthProvider authProvider,
        String role,
        LocalDateTime fechaRegistro
) {
}

