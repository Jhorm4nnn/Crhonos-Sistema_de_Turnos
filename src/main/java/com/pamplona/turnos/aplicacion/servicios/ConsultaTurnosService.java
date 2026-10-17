package com.pamplona.turnos.aplicacion.servicios;

import com.pamplona.turnos.aplicacion.puertos.entrada.Comandos;
import com.pamplona.turnos.aplicacion.puertos.entrada.ConsultarTurnosUseCase;
import com.pamplona.turnos.aplicacion.puertos.salida.TurnoRepositoryPort;
import com.pamplona.turnos.aplicacion.seguridad.Autorizacion;
import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;
import com.pamplona.turnos.dominio.Turno;
import com.pamplona.turnos.excepciones.DatosInvalidosException;

import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** HU-6: consulta de turnos (lectura, separada de TurnoService). */
public class ConsultaTurnosService implements ConsultarTurnosUseCase {

    private final TurnoRepositoryPort turnos;
    private final Clock reloj;

    public ConsultaTurnosService(TurnoRepositoryPort turnos, Clock reloj) {
        this.turnos = turnos;
        this.reloj = reloj;
    }

    @Override
    public List<Turno> ejecutar(UsuarioAutenticado usuario, Comandos.FiltroTurnos f) {
        Autorizacion.exigirAutenticado(usuario, reloj);
        if (f.desde() != null && f.hasta() != null && f.desde().isAfter(f.hasta())) {
            throw new DatosInvalidosException("La fecha 'desde' no puede ser posterior a 'hasta'.");
        }
        return turnos.listar().stream()
                .filter(t -> f.profesionalId() == null || t.getProfesional().getId() == f.profesionalId())
                .filter(t -> f.clienteId() == null || t.getCliente().getId() == f.clienteId())
                .filter(t -> f.desde() == null || !t.getFecha().isBefore(f.desde()))
                .filter(t -> f.hasta() == null || !t.getFecha().isAfter(f.hasta()))
                .sorted(Comparator.comparing(Turno::getInicio))
                .collect(Collectors.toList());
    }
}
