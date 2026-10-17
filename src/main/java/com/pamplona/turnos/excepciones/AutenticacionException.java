package com.pamplona.turnos.excepciones;

/** Fallo al iniciar sesion o sesion invalida/expirada. */
public class AutenticacionException extends TurnosException {

    public AutenticacionException(String mensaje) {
        super(mensaje);
    }
}
