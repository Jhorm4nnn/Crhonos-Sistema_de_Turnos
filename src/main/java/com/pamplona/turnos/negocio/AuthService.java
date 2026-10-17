package com.pamplona.turnos.negocio;

import com.pamplona.turnos.datos.AuthGateway;
import com.pamplona.turnos.datos.AuthGatewayException;
import com.pamplona.turnos.excepciones.AutenticacionException;
import com.pamplona.turnos.excepciones.DatosInvalidosException;

/** Caso de uso: iniciar sesion contra el authcore y guardar el usuario en la sesion. */
public class AuthService {

    private final AuthGateway gateway;
    private final Sesion sesion;

    public AuthService(AuthGateway gateway, Sesion sesion) {
        this.gateway = gateway;
        this.sesion = sesion;
    }

    public UsuarioAutenticado login(String usuario, String clave) {
        if (usuario == null || usuario.isBlank()) {
            throw new DatosInvalidosException("El usuario es obligatorio.");
        }
        if (clave == null || clave.isEmpty()) {
            throw new DatosInvalidosException("La clave es obligatoria.");
        }
        String token;
        try {
            token = gateway.login(usuario.trim(), clave);
        } catch (AuthGatewayException e) {
            switch (e.getTipo()) {
                case CREDENCIALES_INVALIDAS -> throw new AutenticacionException("Usuario o clave incorrectos.");
                case SERVICIO_NO_DISPONIBLE -> throw new AutenticacionException(
                        "No se pudo conectar con el servicio de autenticacion. Verifique que este en ejecucion.");
                default -> throw new AutenticacionException("El servicio de autenticacion respondio algo inesperado.");
            }
        }
        UsuarioAutenticado u = TokenJwt.leer(token);
        sesion.iniciar(u);
        return u;
    }
}
