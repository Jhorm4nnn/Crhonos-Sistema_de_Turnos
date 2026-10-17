package com.pamplona.turnos.modelo;

import com.pamplona.turnos.excepciones.DatosInvalidosException;

import java.time.LocalTime;

/** Objeto de valor: el rango diario en que un profesional atiende. Inmutable. */
public final class HorarioDisponibilidad {

    private final LocalTime inicio;
    private final LocalTime fin;

    public HorarioDisponibilidad(LocalTime inicio, LocalTime fin) {
        if (inicio == null || fin == null) {
            throw new DatosInvalidosException("El horario de disponibilidad es obligatorio.");
        }
        if (!inicio.isBefore(fin)) {
            throw new DatosInvalidosException("La hora de inicio debe ser anterior a la hora de fin.");
        }
        this.inicio = inicio;
        this.fin = fin;
    }

    public LocalTime getInicio() { return inicio; }
    public LocalTime getFin() { return fin; }

    /** Un turno de {@code minutos} que empieza en {@code hora} cabe completo en el horario. */
    public boolean cubre(LocalTime hora, int minutos) {
        LocalTime termina = hora.plusMinutes(minutos);
        return !hora.isBefore(inicio) && !termina.isBefore(hora) && !termina.isAfter(fin);
    }

    @Override
    public String toString() {
        return inicio + " a " + fin;
    }
}
