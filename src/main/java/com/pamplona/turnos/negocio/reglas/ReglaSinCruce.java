package com.pamplona.turnos.negocio.reglas;

import com.pamplona.turnos.dominio.Turno;
import com.pamplona.turnos.excepciones.CruceDeHorarioException;

import java.util.List;

/** HU-4: el profesional no puede tener dos turnos agendados que se solapen (sin importar el cliente). */
public class ReglaSinCruce implements ReglaTurno {

    @Override
    public void validar(SolicitudTurno s, List<Turno> turnosExistentes) {
        for (Turno t : turnosExistentes) {
            boolean esElMismo = s.ignorar() != null && t.getId() == s.ignorar().getId();
            if (!esElMismo && t.estaAgendado() && t.getProfesional().equals(s.profesional())
                    && t.seCruzaCon(s.fecha(), s.hora())) {
                throw new CruceDeHorarioException("El profesional ya tiene el turno #" + t.getId()
                        + " que se cruza (" + t.getFecha() + " " + t.getHora() + " a " + t.getHoraFin() + ").");
            }
        }
    }
}
