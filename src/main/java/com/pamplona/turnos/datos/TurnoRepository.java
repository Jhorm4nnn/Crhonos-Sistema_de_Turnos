package com.pamplona.turnos.datos;

import com.pamplona.turnos.dominio.Turno;

import java.util.List;
import java.util.Optional;

public interface TurnoRepository {

    int siguienteId();

    void guardar(Turno turno);

    /** Persiste los cambios hechos a un turno existente (cancelar / reprogramar). */
    void actualizar(Turno turno);

    Optional<Turno> buscarPorId(int id);

    List<Turno> listar();
}
