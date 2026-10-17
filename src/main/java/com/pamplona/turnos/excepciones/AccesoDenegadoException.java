package com.pamplona.turnos.excepciones;

/** El usuario autenticado no tiene el rol necesario para la operacion. */
public class AccesoDenegadoException extends TurnosException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
