package com.pamplona.turnos.negocio;

import com.pamplona.turnos.datos.TurnoRepository;
import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.dominio.Turno;

import java.time.LocalDate;
import java.time.LocalTime;

/** HU-3 / RF-3: el profesional no puede tener dos turnos agendados que se solapen. */
public class ReglaSinCruce implements ReglaTurno {

    private final TurnoRepository turnos;

    public ReglaSinCruce(TurnoRepository turnos) {
        this.turnos = turnos;
    }

    @Override
    public void validar(Profesional prof, LocalDate fecha, LocalTime hora, Turno ignorar) {
        for (Turno t : turnos.listar()) {
            boolean mismo = ignorar != null && t.getId() == ignorar.getId();
            if (!mismo && t.getProfesionalId() == prof.getId()
                    && t.getEstado() == Turno.Estado.AGENDADO && t.seCruzaCon(fecha, hora)) {
                throw new ReglaNegocioException("El profesional ya tiene el turno #" + t.getId() + " que se cruza ("
                        + t.getFecha() + " " + t.getHora() + " a " + t.getHoraFin() + ").");
            }
        }
    }
}
