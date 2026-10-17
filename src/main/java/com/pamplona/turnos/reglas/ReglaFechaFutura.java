package com.pamplona.turnos.reglas;

import com.pamplona.turnos.dominio.Turno;
import com.pamplona.turnos.excepciones.ReglaTurnoException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/** HU-3: la fecha y hora deben ser posteriores a la actual. */
public class ReglaFechaFutura implements ReglaTurno {

    private final Clock reloj;

    public ReglaFechaFutura(Clock reloj) {
        this.reloj = reloj;
    }

    @Override
    public void validar(SolicitudTurno s, List<Turno> turnosExistentes) {
        if (!LocalDateTime.of(s.fecha(), s.hora()).isAfter(LocalDateTime.now(reloj))) {
            throw new ReglaTurnoException("La fecha y hora deben ser posteriores a la actual.");
        }
    }
}
