package com.pamplona.turnos.excepciones;

/** Un dato obligatorio falta o tiene formato invalido. */
public class DatosInvalidosException extends TurnosException {

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
