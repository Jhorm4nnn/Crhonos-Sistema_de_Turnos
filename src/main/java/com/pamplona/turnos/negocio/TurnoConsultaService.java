package com.pamplona.turnos.negocio;

import com.pamplona.turnos.datos.TurnoRepository;
import com.pamplona.turnos.dominio.Turno;
import com.pamplona.turnos.excepciones.DatosInvalidosException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** HU-6: consulta de turnos (separada de TurnoService: lectura vs. escritura). */
public class TurnoConsultaService {

    private final TurnoRepository turnos;
    private final Sesion sesion;

    public TurnoConsultaService(TurnoRepository turnos, Sesion sesion) {
        this.turnos = turnos;
        this.sesion = sesion;
    }

    /** Cualquier filtro en null se ignora; se pueden combinar. */
    public List<Turno> consultar(Integer profesionalId, Integer clienteId, LocalDate desde, LocalDate hasta) {
        sesion.exigirAutenticado();
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new DatosInvalidosException("La fecha 'desde' no puede ser posterior a 'hasta'.");
        }
        return turnos.listar().stream()
                .filter(t -> profesionalId == null || t.getProfesional().getId() == profesionalId)
                .filter(t -> clienteId == null || t.getCliente().getId() == clienteId)
                .filter(t -> desde == null || !t.getFecha().isBefore(desde))
                .filter(t -> hasta == null || !t.getFecha().isAfter(hasta))
                .sorted(Comparator.comparing(Turno::getInicio))
                .collect(Collectors.toList());
    }

    public List<Turno> listarTodos() {
        return consultar(null, null, null, null);
    }
}
