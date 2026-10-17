package com.pamplona.turnos.negocio.reglas;

import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.dominio.Turno;

import java.time.LocalDate;
import java.time.LocalTime;

/** Datos de un turno que se quiere crear o mover. {@code ignorar} es el turno que se reprograma (o null). */
public record SolicitudTurno(Profesional profesional, LocalDate fecha, LocalTime hora, Turno ignorar) { }
