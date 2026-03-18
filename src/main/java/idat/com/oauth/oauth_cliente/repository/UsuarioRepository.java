package idat.com.oauth.oauth_cliente.repository;

import idat.com.oauth.oauth_cliente.entity.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByGithubId(String githubId);

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);
}

