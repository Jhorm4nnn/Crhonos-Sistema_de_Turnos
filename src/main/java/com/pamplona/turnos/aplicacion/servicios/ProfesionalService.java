package com.pamplona.turnos.aplicacion.servicios;

import com.pamplona.turnos.aplicacion.puertos.entrada.Comandos;
import com.pamplona.turnos.aplicacion.puertos.entrada.ListarProfesionalesUseCase;
import com.pamplona.turnos.aplicacion.puertos.entrada.RegistrarProfesionalUseCase;
import com.pamplona.turnos.aplicacion.puertos.salida.ProfesionalRepositoryPort;
import com.pamplona.turnos.aplicacion.seguridad.Autorizacion;
import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;
import com.pamplona.turnos.dominio.HorarioDisponibilidad;
import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.excepciones.DuplicadoException;

import java.time.Clock;
import java.util.List;

/** Casos de uso de profesionales (HU-2). */
public class ProfesionalService implements RegistrarProfesionalUseCase, ListarProfesionalesUseCase {

    private final ProfesionalRepositoryPort repo;
    private final Clock reloj;

    public ProfesionalService(ProfesionalRepositoryPort repo, Clock reloj) {
        this.repo = repo;
        this.reloj = reloj;
    }

    @Override
    public Profesional ejecutar(UsuarioAutenticado usuario, Comandos.RegistrarProfesional c) {
        Autorizacion.exigirAdmin(usuario, reloj);
        HorarioDisponibilidad horario = new HorarioDisponibilidad(c.horaInicio(), c.horaFin());
        Profesional nuevo = new Profesional(repo.siguienteId(), c.nombre(), c.documento(), c.telefono(), c.correo(),
                c.genero(), horario);
        if (repo.buscarPorDocumento(nuevo.getDocumento()).isPresent()) {
            throw new DuplicadoException("Ya existe un profesional con el documento " + nuevo.getDocumento() + ".");
        }
        repo.guardar(nuevo);
        return nuevo;
    }

    @Override
    public List<Profesional> ejecutar(UsuarioAutenticado usuario) {
        Autorizacion.exigirAutenticado(usuario, reloj);
        return repo.listar();
    }
}
