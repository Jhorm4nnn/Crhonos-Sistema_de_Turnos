package com.pamplona.turnos.excepciones;

/** No existe el registro buscado. */
public class NoEncontradoException extends TurnosException {

    public NoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
