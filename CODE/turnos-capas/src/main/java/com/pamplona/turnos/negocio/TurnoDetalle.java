package com.pamplona.turnos.negocio;

import com.pamplona.turnos.dominio.Turno;

import java.time.LocalDate;
import java.time.LocalTime;

/** Turno con los nombres ya resueltos, listo para mostrarse. */
public record TurnoDetalle(int id, String cliente, String profesional, LocalDate fecha,
                           LocalTime hora, LocalTime horaFin, Turno.Estado estado) {

    public String resumen() {
        return "#" + id + " " + cliente + " con " + profesional + ", " + fecha + " " + hora + "-" + horaFin
                + " (" + estado + ")";
    }
}
