package com.pamplona.turnos.aplicacion.servicios;

import com.pamplona.turnos.aplicacion.puertos.entrada.CancelarTurnoUseCase;
import com.pamplona.turnos.aplicacion.puertos.entrada.Comandos;
import com.pamplona.turnos.aplicacion.puertos.entrada.CrearTurnoUseCase;
import com.pamplona.turnos.aplicacion.puertos.entrada.ReprogramarTurnoUseCase;
import com.pamplona.turnos.aplicacion.puertos.salida.ClienteRepositoryPort;
import com.pamplona.turnos.aplicacion.puertos.salida.ProfesionalRepositoryPort;
import com.pamplona.turnos.aplicacion.puertos.salida.TurnoRepositoryPort;
import com.pamplona.turnos.aplicacion.reglas.ReglaTurno;
import com.pamplona.turnos.aplicacion.reglas.SolicitudTurno;
import com.pamplona.turnos.aplicacion.seguridad.Autorizacion;
import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;
import com.pamplona.turnos.dominio.Cliente;
import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.dominio.Turno;
import com.pamplona.turnos.excepciones.DatosInvalidosException;
import com.pamplona.turnos.excepciones.NoEncontradoException;

import java.time.Clock;
import java.util.List;

/** Casos de uso de turnos (HU-3, HU-4, HU-5). Las reglas se inyectan como lista. */
public class TurnoService implements CrearTurnoUseCase, CancelarTurnoUseCase, ReprogramarTurnoUseCase {

    private final TurnoRepositoryPort turnos;
    private final ClienteRepositoryPort clientes;
    private final ProfesionalRepositoryPort profesionales;
    private final List<ReglaTurno> reglas;
    private final Clock reloj;

    public TurnoService(TurnoRepositoryPort turnos, ClienteRepositoryPort clientes,
                        ProfesionalRepositoryPort profesionales, List<ReglaTurno> reglas, Clock reloj) {
        this.turnos = turnos;
        this.clientes = clientes;
        this.profesionales = profesionales;
        this.reglas = List.copyOf(reglas);
        this.reloj = reloj;
    }

    @Override
    public Turno ejecutar(UsuarioAutenticado usuario, Comandos.CrearTurno c) {
        Autorizacion.exigirAdmin(usuario, reloj);
        if (c.clienteId() == null) {
            throw new DatosInvalidosException("Debe seleccionar un cliente.");
        }
        if (c.profesionalId() == null) {
            throw new DatosInvalidosException("Debe seleccionar un profesional.");
        }
        Cliente cliente = clientes.buscarPorId(c.clienteId())
                .orElseThrow(() -> new NoEncontradoException("No existe un cliente con id " + c.clienteId() + "."));
        Profesional profesional = profesionales.buscarPorId(c.profesionalId())
                .orElseThrow(() -> new NoEncontradoException("No existe un profesional con id " + c.profesionalId() + "."));
        validar(new SolicitudTurno(profesional, c.fecha(), c.hora(), null));

        Turno turno = new Turno(turnos.siguienteId(), cliente, profesional, c.fecha(), c.hora());
        turnos.guardar(turno);
        return turno;
    }

    @Override
    public Turno ejecutar(UsuarioAutenticado usuario, Integer turnoId) {
        Autorizacion.exigirAdmin(usuario, reloj);
        Turno turno = buscar(turnoId);
        turno.cancelar();
        turnos.actualizar(turno);
        return turno;
    }

    @Override
    public Turno ejecutar(UsuarioAutenticado usuario, Comandos.ReprogramarTurno c) {
        Autorizacion.exigirAdmin(usuario, reloj);
        Turno turno = buscar(c.turnoId());
        turno.exigirAgendado();
        validar(new SolicitudTurno(turno.getProfesional(), c.fecha(), c.hora(), turno));
        turno.reprogramar(c.fecha(), c.hora());
        turnos.actualizar(turno);
        return turno;
    }

    private void validar(SolicitudTurno solicitud) {
        if (solicitud.fecha() == null || solicitud.hora() == null) {
            throw new DatosInvalidosException("La fecha y la hora son obligatorias.");
        }
        List<Turno> existentes = turnos.listar();
        for (ReglaTurno regla : reglas) {
            regla.validar(solicitud, existentes);
        }
    }

    private Turno buscar(Integer turnoId) {
        if (turnoId == null) {
            throw new DatosInvalidosException("Seleccione un turno.");
        }
        return turnos.buscarPorId(turnoId)
                .orElseThrow(() -> new NoEncontradoException("No existe un turno con id " + turnoId + "."));
    }
}
