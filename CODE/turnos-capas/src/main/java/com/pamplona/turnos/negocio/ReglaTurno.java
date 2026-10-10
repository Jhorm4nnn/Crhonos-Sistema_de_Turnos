package com.pamplona.turnos.negocio;

import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.dominio.Turno;

import java.time.LocalDate;
import java.time.LocalTime;

/** Una regla que debe cumplir un turno. Agregar una regla nueva = crear otra clase (principio Abierto/Cerrado). */
public interface ReglaTurno {

    /** @param ignorar turno que se esta reprogramando (no debe compararse contra si mismo); puede ser null. */
    void validar(Profesional profesional, LocalDate fecha, LocalTime hora, Turno ignorar);
}
