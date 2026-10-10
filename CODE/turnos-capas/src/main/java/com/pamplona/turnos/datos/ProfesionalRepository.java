package com.pamplona.turnos.datos;

import com.pamplona.turnos.dominio.Profesional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ProfesionalRepository {

    Profesional guardar(String nombre, LocalTime inicio, LocalTime fin);

    Optional<Profesional> buscarPorId(int id);

    List<Profesional> listar();
}
