package com.pamplona.turnos.negocio;

import com.pamplona.turnos.datos.ClienteRepository;
import com.pamplona.turnos.datos.ProfesionalRepository;
import com.pamplona.turnos.datos.TurnoRepository;
import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.dominio.Turno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/** HU-2, HU-3, HU-4: crear, cancelar y reprogramar turnos. Las reglas se inyectan como lista. */
public class TurnoService {

    private final TurnoRepository turnos;
    private final ClienteRepository clientes;
    private final ProfesionalRepository profesionales;
    private final List<ReglaTurno> reglas;
    private final TurnoDetalleAssembler assembler;
    private final Sesion sesion;

    public TurnoService(TurnoRepository turnos, ClienteRepository clientes, ProfesionalRepository profesionales,
                        List<ReglaTurno> reglas, Sesion sesion) {
        this.turnos = turnos;
        this.clientes = clientes;
        this.profesionales = profesionales;
        this.reglas = reglas;
        this.assembler = new TurnoDetalleAssembler(clientes, profesionales);
        this.sesion = sesion;
    }

    public TurnoDetalle crear(Integer clienteId, Integer profesionalId, LocalDate fecha, LocalTime hora) {
        sesion.exigirAdmin();
        if (clienteId == null || clientes.buscarPorId(clienteId).isEmpty()) {
            throw new ReglaNegocioException("Debe seleccionar un cliente existente.");
        }
        Profesional prof = (profesionalId == null) ? null : profesionales.buscarPorId(profesionalId).orElse(null);
        if (prof == null) {
            throw new ReglaNegocioException("Debe seleccionar un profesional existente.");
        }
        validar(prof, fecha, hora, null);
        return assembler.de(turnos.guardar(clienteId, profesionalId, fecha, hora));
    }

    public TurnoDetalle cancelar(Integer turnoId) {
        sesion.exigirAdmin();
        Turno t = buscarAgendado(turnoId);
        t.cancelar();
        turnos.actualizar(t);
        return assembler.de(t);
    }

    public TurnoDetalle reprogramar(Integer turnoId, LocalDate nuevaFecha, LocalTime nuevaHora) {
        sesion.exigirAdmin();
        Turno t = buscarAgendado(turnoId);
        Profesional prof = profesionales.buscarPorId(t.getProfesionalId())
                .orElseThrow(() -> new ReglaNegocioException("El profesional del turno ya no existe."));
        validar(prof, nuevaFecha, nuevaHora, t);
        t.reprogramar(nuevaFecha, nuevaHora);
        turnos.actualizar(t);
        return assembler.de(t);
    }

    private void validar(Profesional prof, LocalDate fecha, LocalTime hora, Turno ignorar) {
        if (fecha == null || hora == null) {
            throw new ReglaNegocioException("La fecha y la hora son obligatorias.");
        }
        for (ReglaTurno regla : reglas) {
            regla.validar(prof, fecha, hora, ignorar);
        }
    }

    private Turno buscarAgendado(Integer turnoId) {
        if (turnoId == null) {
            throw new ReglaNegocioException("Seleccione un turno de la tabla.");
        }
        Turno t = turnos.buscarPorId(turnoId)
                .orElseThrow(() -> new ReglaNegocioException("No existe un turno con id " + turnoId + "."));
        if (t.getEstado() == Turno.Estado.CANCELADO) {
            throw new ReglaNegocioException("El turno " + turnoId + " ya esta cancelado.");
        }
        return t;
    }
}
