package com.pamplona.turnos.negocio;

import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.dominio.Turno;

import java.time.LocalDate;
import java.time.LocalTime;

/** El turno completo debe caber en el horario de disponibilidad del profesional. */
public class ReglaDisponibilidad implements ReglaTurno {

    @Override
    public void validar(Profesional prof, LocalDate fecha, LocalTime hora, Turno ignorar) {
        LocalTime fin = hora.plusMinutes(Turno.DURACION_MIN);
        if (hora.isBefore(prof.getHoraInicio()) || fin.isAfter(prof.getHoraFin()) || fin.isBefore(hora)) {
            throw new ReglaNegocioException("El horario esta fuera de la disponibilidad del profesional ("
                    + prof.getHoraInicio() + " a " + prof.getHoraFin() + ").");
        }
    }
}
