package idat.com.oauth.oauth_cliente.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroUsuarioRequest(
        @NotBlank @Size(max = 100) String username,
        @NotBlank @Size(min = 6, max = 120) String password,
        @Size(max = 100) String email
) {
}
