package com.pamplona.turnos.negocio;

import com.pamplona.turnos.datos.ClienteRepository;
import com.pamplona.turnos.datos.ProfesionalRepository;
import com.pamplona.turnos.dominio.Cliente;
import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.dominio.Turno;

/** Unica responsabilidad: convertir un Turno en TurnoDetalle (resolver nombres). */
class TurnoDetalleAssembler {

    private final ClienteRepository clientes;
    private final ProfesionalRepository profesionales;

    TurnoDetalleAssembler(ClienteRepository clientes, ProfesionalRepository profesionales) {
        this.clientes = clientes;
        this.profesionales = profesionales;
    }

    TurnoDetalle de(Turno t) {
        String cliente = clientes.buscarPorId(t.getClienteId()).map(Cliente::getNombre)
                .orElse("(id " + t.getClienteId() + ")");
        String prof = profesionales.buscarPorId(t.getProfesionalId()).map(Profesional::getNombre)
                .orElse("(id " + t.getProfesionalId() + ")");
        return new TurnoDetalle(t.getId(), cliente, prof, t.getFecha(), t.getHora(), t.getHoraFin(), t.getEstado());
    }
}
