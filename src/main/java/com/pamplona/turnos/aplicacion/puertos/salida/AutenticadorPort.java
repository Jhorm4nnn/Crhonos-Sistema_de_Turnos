package com.pamplona.turnos.aplicacion.puertos.salida;

/** Pide un token al servicio de autenticacion (authcore). */
public interface AutenticadorPort {

    /** @return el token JWT. @throws com.pamplona.turnos.excepciones.AutenticacionException si no se pudo autenticar. */
    String login(String usuario, String clave);
}
