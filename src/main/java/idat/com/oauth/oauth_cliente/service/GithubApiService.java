package idat.com.oauth.oauth_cliente.service;

import idat.com.oauth.oauth_cliente.dto.RepositorioDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface GithubApiService {

    List<RepositorioDTO> obtenerRepositoriosUsuarioAutenticado();

    RepositorioDTO obtenerRepositorioPorNombre(String nombreRepositorio);
}
