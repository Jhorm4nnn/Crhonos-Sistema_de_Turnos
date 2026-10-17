package com.pamplona.turnos.negocio;

import com.pamplona.turnos.datos.ClienteRepository;
import com.pamplona.turnos.datos.ProfesionalRepository;
import com.pamplona.turnos.datos.TurnoRepository;
import com.pamplona.turnos.dominio.Cliente;
import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.dominio.Turno;
import com.pamplona.turnos.excepciones.DatosInvalidosException;
import com.pamplona.turnos.excepciones.NoEncontradoException;
import com.pamplona.turnos.negocio.reglas.ReglaTurno;
import com.pamplona.turnos.negocio.reglas.SolicitudTurno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/** HU-3, HU-4, HU-5: crear, cancelar y reprogramar turnos. Las reglas se inyectan como lista. */
public class TurnoService {

    private final TurnoRepository turnos;
    private final ClienteRepository clientes;
    private final ProfesionalRepository profesionales;
    private final List<ReglaTurno> reglas;
    private final Sesion sesion;

    public TurnoService(TurnoRepository turnos, ClienteRepository clientes, ProfesionalRepository profesionales,
                        List<ReglaTurno> reglas, Sesion sesion) {
        this.turnos = turnos;
        this.clientes = clientes;
        this.profesionales = profesionales;
        this.reglas = List.copyOf(reglas);
        this.sesion = sesion;
    }

    public Turno crear(Integer clienteId, Integer profesionalId, LocalDate fecha, LocalTime hora) {
        sesion.exigirAdmin();
        if (clienteId == null) {
            throw new DatosInvalidosException("Debe seleccionar un cliente.");
        }
        if (profesionalId == null) {
            throw new DatosInvalidosException("Debe seleccionar un profesional.");
        }
        Cliente cliente = clientes.buscarPorId(clienteId)
                .orElseThrow(() -> new NoEncontradoException("No existe un cliente con id " + clienteId + "."));
        Profesional profesional = profesionales.buscarPorId(profesionalId)
                .orElseThrow(() -> new NoEncontradoException("No existe un profesional con id " + profesionalId + "."));
        validar(new SolicitudTurno(profesional, fecha, hora, null));

        Turno turno = new Turno(turnos.siguienteId(), cliente, profesional, fecha, hora);
        turnos.guardar(turno);
        return turno;
    }

    public Turno cancelar(Integer turnoId) {
        sesion.exigirAdmin();
        Turno turno = buscar(turnoId);
        turno.cancelar();
        turnos.actualizar(turno);
        return turno;
    }

    public Turno reprogramar(Integer turnoId, LocalDate nuevaFecha, LocalTime nuevaHora) {
        sesion.exigirAdmin();
        Turno turno = buscar(turnoId);
        turno.exigirAgendado();
        validar(new SolicitudTurno(turno.getProfesional(), nuevaFecha, nuevaHora, turno));
        turno.reprogramar(nuevaFecha, nuevaHora);
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
            throw new DatosInvalidosException("Seleccione un turno de la tabla.");
        }
        return turnos.buscarPorId(turnoId)
                .orElseThrow(() -> new NoEncontradoException("No existe un turno con id " + turnoId + "."));
    }
}
