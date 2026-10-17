package com.pamplona.turnos.negocio;

import com.pamplona.turnos.datos.ProfesionalRepository;
import com.pamplona.turnos.dominio.Genero;
import com.pamplona.turnos.dominio.HorarioDisponibilidad;
import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.excepciones.DuplicadoException;

import java.time.LocalTime;
import java.util.List;

/** HU-2: profesionales y su horario de disponibilidad. */
public class ProfesionalService {

    private final ProfesionalRepository repo;
    private final Sesion sesion;

    public ProfesionalService(ProfesionalRepository repo, Sesion sesion) {
        this.repo = repo;
        this.sesion = sesion;
    }

    public Profesional registrar(String nombre, String documento, String telefono, String correo, Genero genero,
                                 LocalTime horaInicio, LocalTime horaFin) {
        sesion.exigirAdmin();
        HorarioDisponibilidad horario = new HorarioDisponibilidad(horaInicio, horaFin);
        Profesional nuevo = new Profesional(repo.siguienteId(), nombre, documento, telefono, correo, genero, horario);
        if (repo.buscarPorDocumento(nuevo.getDocumento()).isPresent()) {
            throw new DuplicadoException("Ya existe un profesional con el documento " + nuevo.getDocumento() + ".");
        }
        repo.guardar(nuevo);
        return nuevo;
    }

    public List<Profesional> listar() {
        sesion.exigirAutenticado();
        return repo.listar();
    }
}
