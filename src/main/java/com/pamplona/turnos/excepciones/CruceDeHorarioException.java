package com.pamplona.turnos.excepciones;

/** HU-4: el profesional ya tiene un turno en ese rango. Es un caso particular de ReglaTurnoException. */
public class CruceDeHorarioException extends ReglaTurnoException {

    public CruceDeHorarioException(String mensaje) {
        super(mensaje);
    }
}
