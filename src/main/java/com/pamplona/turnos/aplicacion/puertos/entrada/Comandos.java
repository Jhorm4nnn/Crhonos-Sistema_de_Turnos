package com.pamplona.turnos.aplicacion.puertos.entrada;

import com.pamplona.turnos.dominio.Genero;

import java.time.LocalDate;
import java.time.LocalTime;

/** Datos que cada caso de uso necesita. Son simples contenedores, sin logica. */
public final class Comandos {

    private Comandos() { }

    public record RegistrarCliente(String nombre, String documento, String telefono, String correo, Genero genero) { }

    public record RegistrarProfesional(String nombre, String documento, String telefono, String correo,
                                       Genero genero, LocalTime horaInicio, LocalTime horaFin) { }

    public record CrearTurno(Integer clienteId, Integer profesionalId, LocalDate fecha, LocalTime hora) { }

    public record ReprogramarTurno(Integer turnoId, LocalDate fecha, LocalTime hora) { }

    /** Cualquier campo en null se ignora. */
    public record FiltroTurnos(Integer profesionalId, Integer clienteId, LocalDate desde, LocalDate hasta) { }
}
