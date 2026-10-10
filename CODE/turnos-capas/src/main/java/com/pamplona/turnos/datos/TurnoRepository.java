package com.pamplona.turnos.datos;

import com.pamplona.turnos.dominio.Turno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TurnoRepository {

    Turno guardar(int clienteId, int profesionalId, LocalDate fecha, LocalTime hora);

    /** Persiste los cambios hechos a un turno existente (cancelar / reprogramar). */
    void actualizar(Turno turno);

    Optional<Turno> buscarPorId(int id);

    List<Turno> listar();
}
