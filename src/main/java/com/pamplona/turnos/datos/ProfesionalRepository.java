package com.pamplona.turnos.datos;

import com.pamplona.turnos.dominio.Profesional;

import java.util.List;
import java.util.Optional;

public interface ProfesionalRepository {

    int siguienteId();

    void guardar(Profesional profesional);

    Optional<Profesional> buscarPorId(int id);

    Optional<Profesional> buscarPorDocumento(String documento);

    List<Profesional> listar();
}
