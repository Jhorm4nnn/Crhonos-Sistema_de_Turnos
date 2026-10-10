package com.pamplona.turnos.negocio;

import com.pamplona.turnos.datos.ClienteRepository;
import com.pamplona.turnos.datos.ProfesionalRepository;
import com.pamplona.turnos.datos.TurnoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/** HU-5 / RF-5: consulta de turnos (separada de TurnoService: lectura vs. escritura). */
public class TurnoConsultaService {

    private final TurnoRepository turnos;
    private final TurnoDetalleAssembler assembler;
    private final Sesion sesion;

    public TurnoConsultaService(TurnoRepository turnos, ClienteRepository clientes,
                                ProfesionalRepository profesionales, Sesion sesion) {
        this.turnos = turnos;
        this.assembler = new TurnoDetalleAssembler(clientes, profesionales);
        this.sesion = sesion;
    }

    /** Cualquier filtro en null se ignora; se pueden combinar. */
    public List<TurnoDetalle> consultar(Integer profesionalId, Integer clienteId, LocalDate desde, LocalDate hasta) {
        sesion.exigirAutenticado();
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ReglaNegocioException("La fecha 'desde' no puede ser posterior a 'hasta'.");
        }
        return turnos.listar().stream()
                .filter(t -> profesionalId == null || t.getProfesionalId() == profesionalId)
                .filter(t -> clienteId == null || t.getClienteId() == clienteId)
                .filter(t -> desde == null || !t.getFecha().isBefore(desde))
                .filter(t -> hasta == null || !t.getFecha().isAfter(hasta))
                .sorted((a, b) -> a.getInicio().compareTo(b.getInicio()))
                .map(assembler::de)
                .collect(Collectors.toList());
    }

    public List<TurnoDetalle> listarTodos() {
        return consultar(null, null, null, null);
    }
}
