package com.pamplona.turnos.excepciones;

/** Ya existe un registro con ese documento. */
public class DuplicadoException extends TurnosException {

    public DuplicadoException(String mensaje) {
        super(mensaje);
    }
}
