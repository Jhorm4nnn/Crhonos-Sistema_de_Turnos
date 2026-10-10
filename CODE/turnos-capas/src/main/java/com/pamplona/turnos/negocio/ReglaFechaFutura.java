package com.pamplona.turnos.negocio;

import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.dominio.Turno;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** HU-2: la fecha y hora deben ser posteriores a la actual. */
public class ReglaFechaFutura implements ReglaTurno {

    private final Clock reloj;

    public ReglaFechaFutura(Clock reloj) {
        this.reloj = reloj;
    }

    @Override
    public void validar(Profesional profesional, LocalDate fecha, LocalTime hora, Turno ignorar) {
        if (!LocalDateTime.of(fecha, hora).isAfter(LocalDateTime.now(reloj))) {
            throw new ReglaNegocioException("La fecha y hora deben ser posteriores a la actual.");
        }
    }
}
