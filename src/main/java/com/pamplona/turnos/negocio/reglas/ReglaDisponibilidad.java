package com.pamplona.turnos.negocio.reglas;

import com.pamplona.turnos.dominio.Turno;
import com.pamplona.turnos.excepciones.ReglaTurnoException;

import java.util.List;

/** El turno completo debe caber en el horario de disponibilidad del profesional. */
public class ReglaDisponibilidad implements ReglaTurno {

    @Override
    public void validar(SolicitudTurno s, List<Turno> turnosExistentes) {
        if (!s.profesional().getHorario().cubre(s.hora(), Turno.DURACION_MIN)) {
            throw new ReglaTurnoException("El horario esta fuera de la disponibilidad del profesional ("
                    + s.profesional().getHorario() + ").");
        }
    }
}
