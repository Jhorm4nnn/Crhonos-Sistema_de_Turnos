package com.pamplona.turnos.dominio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Turno {

    public enum Estado { AGENDADO, CANCELADO }

    /** Duracion fija de cada turno, en minutos. */
    public static final int DURACION_MIN = 30;

    private final int id;
    private final int clienteId;
    private final int profesionalId;
    private LocalDate fecha;
    private LocalTime hora;
    private Estado estado;

    public Turno(int id, int clienteId, int profesionalId, LocalDate fecha, LocalTime hora, Estado estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.profesionalId = profesionalId;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
    }

    public int getId() { return id; }
    public int getClienteId() { return clienteId; }
    public int getProfesionalId() { return profesionalId; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public LocalTime getHoraFin() { return hora.plusMinutes(DURACION_MIN); }
    public Estado getEstado() { return estado; }

    public LocalDateTime getInicio() { return LocalDateTime.of(fecha, hora); }

    public void cancelar() { this.estado = Estado.CANCELADO; }

    public void reprogramar(LocalDate nuevaFecha, LocalTime nuevaHora) {
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
}
