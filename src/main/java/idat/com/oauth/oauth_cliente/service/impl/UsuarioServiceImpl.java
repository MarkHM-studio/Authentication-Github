package idat.com.oauth.oauth_cliente.service.impl;

import idat.com.oauth.oauth_cliente.dto.RegistroUsuarioRequest;
import idat.com.oauth.oauth_cliente.dto.UsuarioDTO;
import idat.com.oauth.oauth_cliente.entity.Usuario;
import idat.com.oauth.oauth_cliente.enumtype.AuthProvider;
import idat.com.oauth.oauth_cliente.exception.BadRequestException;
import idat.com.oauth.oauth_cliente.exception.ResourceNotFoundException;
import idat.com.oauth.oauth_cliente.repository.UsuarioRepository;
import idat.com.oauth.oauth_cliente.service.UsuarioService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioDTO registrarUsuarioLocal(RegistroUsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new BadRequestException("El username ya existe");
        }

        Usuario usuario = Usuario.builder()
                .githubId("LOCAL_" + request.username())
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .authProvider(AuthProvider.LOCAL)
                .role("ROLE_USER")
                .fechaRegistro(LocalDateTime.now())
                .build();

        return toDto(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO obtenerPerfilAutenticado() {
        return toDto(obtenerEntidadAutenticada());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerEntidadAutenticada() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ResourceNotFoundException("No hay usuario autenticado");
        }

        return usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no existe en BD"));
    }

    @Override
    @Transactional
    public Usuario upsertGithubUser(String githubId, String username, String email, String avatarUrl) {
        Usuario usuario = usuarioRepository.findByGithubId(githubId)
                .orElse(Usuario.builder()
                        .githubId(githubId)
                        .fechaRegistro(LocalDateTime.now())
                        .build());

        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setAvatarUrl(avatarUrl);
        usuario.setAuthProvider(AuthProvider.GITHUB);
        if (usuario.getRole() == null || usuario.getRole().isBlank()) {
            usuario.setRole("ROLE_USER");
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void actualizarTokenGithub(String githubId, String accessToken) {
        Usuario usuario = usuarioRepository.findByGithubId(githubId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe usuario GitHub para guardar token"));

        usuario.setAccessToken(accessToken);
        usuarioRepository.save(usuario);
    }

    private UsuarioDTO toDto(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getGithubId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getAvatarUrl(),
                usuario.getAuthProvider(),
                usuario.getRole(),
                usuario.getFechaRegistro()
        );
    }
}
