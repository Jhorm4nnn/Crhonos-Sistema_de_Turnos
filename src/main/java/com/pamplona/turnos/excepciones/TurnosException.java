package com.pamplona.turnos.excepciones;

/** Raiz de los errores del sistema. Su mensaje siempre es claro para el usuario (HU-8). */
public class TurnosException extends RuntimeException {

    public TurnosException(String mensaje) {
        super(mensaje);
    }
}
