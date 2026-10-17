package com.pamplona.turnos.modelo;

import com.pamplona.turnos.excepciones.ReglaTurnoException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/** Un turno asocia (composicion) un cliente con un profesional en una fecha y hora. */
public class Turno {

    public enum Estado { AGENDADO, CANCELADO }

    /** Duracion fija de cada turno, en minutos. */
    public static final int DURACION_MIN = 30;

    private final int id;
    private final Cliente cliente;
    private final Profesional profesional;
    private LocalDate fecha;
    private LocalTime hora;
    private Estado estado = Estado.AGENDADO;

    public Turno(int id, Cliente cliente, Profesional profesional, LocalDate fecha, LocalTime hora) {
        this.id = id;
        this.cliente = cliente;
        this.profesional = profesional;
        this.fecha = fecha;
        this.hora = hora;
    }

    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Profesional getProfesional() { return profesional; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public LocalTime getHoraFin() { return hora.plusMinutes(DURACION_MIN); }
    public Estado getEstado() { return estado; }
    public boolean estaAgendado() { return estado == Estado.AGENDADO; }
    public LocalDateTime getInicio() { return LocalDateTime.of(fecha, hora); }

    /** HU-5: un turno cancelado no se puede cancelar otra vez. */
    public void cancelar() {
        exigirAgendado();
        estado = Estado.CANCELADO;
    }

    /** HU-5: solo se reprograma un turno agendado. La validacion de cruces la hace la Agenda. */
    public void reprogramar(LocalDate nuevaFecha, LocalTime nuevaHora) {
        exigirAgendado();
        this.fecha = nuevaFecha;
        this.hora = nuevaHora;
    }

    /** Regla de dominio: dos turnos se cruzan si comparten fecha y sus rangos se solapan. */
    public boolean seCruzaCon(LocalDate otraFecha, LocalTime otraHora) {
        if (!fecha.equals(otraFecha)) {
            return false;
        }
        LocalTime otraFin = otraHora.plusMinutes(DURACION_MIN);
        return hora.isBefore(otraFin) && otraHora.isBefore(getHoraFin());
    }

    /** @throws ReglaTurnoException si el turno ya esta cancelado. */
    public void exigirAgendado() {
        if (!estaAgendado()) {
            throw new ReglaTurnoException("El turno " + id + " ya esta cancelado.");
        }
    }

    @Override
    public String toString() {
        return "Turno #" + id + " | " + cliente.getNombreCompleto() + " con " + profesional.getNombreCompleto()
                + " | " + fecha + " " + hora + "-" + getHoraFin() + " | " + estado;
    }
}
