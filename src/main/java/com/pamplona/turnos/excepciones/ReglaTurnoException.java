package com.pamplona.turnos.excepciones;

/** Se incumple una regla de negocio del turno. */
public class ReglaTurnoException extends TurnosException {

    public ReglaTurnoException(String mensaje) {
        super(mensaje);
    }
}
