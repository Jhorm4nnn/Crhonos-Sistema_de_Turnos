package com.pamplona.turnos.dominio;

import java.time.LocalTime;

public class Profesional {

    private final int id;
    private final String nombre;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;

    public Profesional(int id, String nombre, LocalTime horaInicio, LocalTime horaFin) {
        this.id = id;
        this.nombre = nombre;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre + " (" + horaInicio + " a " + horaFin + ")";
    }
}
