package com.pamplona.turnos.datos;

/** Acceso al servicio de autenticacion (authcore-service). */
public interface AuthGateway {

    /** @return el token JWT emitido por el authcore. */
    String login(String usuario, String clave);
}
