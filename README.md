# OAuth Cliente

Aplicacion Spring Boot que implementa autenticacion OAuth2 con GitHub usando Spring Security.

## Tecnologias

- Java 17
- Spring Boot 3.5.7
- Spring Security
- Spring OAuth2 Client
- Maven

## Estructura del Proyecto

```
src/main/java/idat/com/oauth/oauth_cliente/
├── OauthClienteApplication.java          # Clase principal
├── config/
│   └── SecurityConfig.java               # Configuracion de seguridad y OAuth2
├── controller/
│   └── HomeController.java               # Controlador REST
└── exception/
    └── GlobalExceptionHandler.java       # Manejo global de excepciones
```

## Endpoints

| Metodo | Ruta             | Acceso  | Descripcion                                      |
|--------|------------------|---------|--------------------------------------------------|
| GET    | `/hello`         | Publico | Retorna un saludo publico                        |
| GET    | `/hellosecured`  | Privado | Retorna un saludo personalizado con datos de GitHub |
| GET    | `/repos`         | Privado | Lista los repositorios del usuario autenticado en GitHub |

## Configuracion

La aplicacion corre en el puerto **8090**. Editar `src/main/resources/application.properties` para configurar:

- Puerto del servidor
- Credenciales de usuario por defecto (form login)
- Client ID y Client Secret de GitHub OAuth2

### Registrar una aplicacion OAuth en GitHub

1. Ir a **GitHub > Settings > Developer settings > OAuth Apps > New OAuth App**
2. Configurar la **Authorization callback URL** como: `http://localhost:8090/login/oauth2/code/github`
3. Copiar el **Client ID** y **Client Secret** generados en `application.properties`:

```properties
spring.security.oauth2.client.registration.github.client-id=TU_CLIENT_ID
spring.security.oauth2.client.registration.github.client-secret=TU_CLIENT_SECRET
```

## Ejecucion

```bash
./mvnw spring-boot:run
```

La aplicacion estara disponible en: `http://localhost:8090`

## Flujo de Autenticacion

1. Acceder a `/hello` -- respuesta publica, sin autenticacion
2. Acceder a `/hellosecured` -- redirige al login
3. Elegir autenticacion:
   - **Form login** con usuario y contrasena configurados
   - **GitHub OAuth2** -- redirige a GitHub para autorizar
4. Tras autenticarse con GitHub, se muestra un saludo con el nombre de usuario de GitHub
5. Acceder a `/repos` -- lista los repositorios del usuario con nombre, descripcion, URL, lenguaje y visibilidad

## Manejo de Errores

La aplicacion cuenta con un manejador global de excepciones (`GlobalExceptionHandler`) que retorna respuestas JSON estandarizadas:

**404 - Endpoint no encontrado:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "El endpoint solicitado no existe",
  "timestamp": "2026-03-04T10:00:00"
}
```

**500 - Error interno:**
```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "Descripcion del error",
  "timestamp": "2026-03-04T10:00:00"
}
```
