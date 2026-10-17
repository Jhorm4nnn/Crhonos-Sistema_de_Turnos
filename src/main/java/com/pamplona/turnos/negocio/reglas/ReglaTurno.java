package com.pamplona.turnos.negocio.reglas;

import com.pamplona.turnos.dominio.Turno;

import java.util.List;

/** Abstraccion: cualquier regla que debe cumplir un turno. Cada implementacion decide como validar. */
public interface ReglaTurno {

    /** @throws com.pamplona.turnos.excepciones.ReglaTurnoException si la solicitud incumple la regla. */
    void validar(SolicitudTurno solicitud, List<Turno> turnosExistentes);
}
