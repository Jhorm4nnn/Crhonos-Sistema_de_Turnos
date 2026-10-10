package com.pamplona.turnos.negocio;

import com.pamplona.turnos.datos.ProfesionalRepository;
import com.pamplona.turnos.dominio.Profesional;

import java.time.LocalTime;
import java.util.List;

/** HU-1 / RF-1: profesionales y su horario de disponibilidad. */
public class ProfesionalService {

    private final ProfesionalRepository repo;
    private final Sesion sesion;

    public ProfesionalService(ProfesionalRepository repo, Sesion sesion) {
        this.repo = repo;
        this.sesion = sesion;
    }

    public Profesional registrar(String nombre, LocalTime inicio, LocalTime fin) {
        sesion.exigirAdmin();
        nombre = Textos.obligatorio(nombre, "El nombre");
        if (inicio == null || fin == null) {
            throw new ReglaNegocioException("El horario de disponibilidad es obligatorio.");
        }
        if (!inicio.isBefore(fin)) {
            throw new ReglaNegocioException("La hora de inicio debe ser anterior a la hora de fin.");
        }
        for (Profesional p : repo.listar()) {
            if (p.getNombre().equalsIgnoreCase(nombre)) {
                throw new ReglaNegocioException("Ya existe un profesional con ese nombre.");
            }
        }
        return repo.guardar(nombre, inicio, fin);
    }

    public List<Profesional> listar() {
        sesion.exigirAutenticado();
        return repo.listar();
    }
}
