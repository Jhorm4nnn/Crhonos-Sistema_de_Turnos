package com.pamplona.turnos.gestion;

import com.pamplona.turnos.dominio.Cliente;
import com.pamplona.turnos.dominio.Genero;
import com.pamplona.turnos.dominio.HorarioDisponibilidad;
import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.dominio.Turno;
import com.pamplona.turnos.excepciones.DatosInvalidosException;
import com.pamplona.turnos.excepciones.DuplicadoException;
import com.pamplona.turnos.excepciones.NoEncontradoException;
import com.pamplona.turnos.reglas.ReglaTurno;
import com.pamplona.turnos.reglas.SolicitudTurno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Coordina a los objetos del dominio: guarda clientes, profesionales y turnos (en memoria)
 * y aplica las reglas de turno. Las reglas se reciben por constructor, asi se pueden agregar
 * otras sin modificar esta clase.
 */
public class Agenda {

    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Profesional> profesionales = new ArrayList<>();
    private final List<Turno> turnos = new ArrayList<>();
    private final List<ReglaTurno> reglas;

    private int siguienteIdCliente = 1;
    private int siguienteIdProfesional = 1;
    private int siguienteIdTurno = 1;

    public Agenda(List<ReglaTurno> reglas) {
        this.reglas = List.copyOf(reglas);
    }

    // ---- HU-1 ----
    public Cliente registrarCliente(String nombre, String documento, String telefono, String correo, Genero genero) {
        Cliente nuevo = new Cliente(siguienteIdCliente, nombre, documento, telefono, correo, genero);
        if (clientes.contains(nuevo)) {
            throw new DuplicadoException("Ya existe un cliente con el documento " + nuevo.getDocumento() + ".");
        }
        siguienteIdCliente++;
        clientes.add(nuevo);
        return nuevo;
    }

    // ---- HU-2 ----
    public Profesional registrarProfesional(String nombre, String documento, String telefono, String correo,
                                            Genero genero, LocalTime horaInicio, LocalTime horaFin) {
        Profesional nuevo = new Profesional(siguienteIdProfesional, nombre, documento, telefono, correo, genero,
                new HorarioDisponibilidad(horaInicio, horaFin));
        if (profesionales.contains(nuevo)) {
            throw new DuplicadoException("Ya existe un profesional con el documento " + nuevo.getDocumento() + ".");
        }
        siguienteIdProfesional++;
        profesionales.add(nuevo);
        return nuevo;
    }

    // ---- HU-3 y HU-4 ----
    public Turno crearTurno(int clienteId, int profesionalId, LocalDate fecha, LocalTime hora) {
        Cliente cliente = buscarCliente(clienteId);
        Profesional profesional = buscarProfesional(profesionalId);
        exigirFechaHora(fecha, hora);
        validarReglas(new SolicitudTurno(profesional, fecha, hora, null));

        Turno turno = new Turno(siguienteIdTurno++, cliente, profesional, fecha, hora);
        turnos.add(turno);
        return turno;
    }

    // ---- HU-5 ----
    public Turno cancelarTurno(int turnoId) {
        Turno turno = buscarTurno(turnoId);
        turno.cancelar();
        return turno;
    }

    public Turno reprogramarTurno(int turnoId, LocalDate nuevaFecha, LocalTime nuevaHora) {
        Turno turno = buscarTurno(turnoId);
        exigirFechaHora(nuevaFecha, nuevaHora);
        turno.exigirAgendado();
        validarReglas(new SolicitudTurno(turno.getProfesional(), nuevaFecha, nuevaHora, turno));
        turno.reprogramar(nuevaFecha, nuevaHora);
        return turno;
    }

    // ---- HU-6 ----
    /** Cualquier filtro en null se ignora; se pueden combinar. */
    public List<Turno> consultarTurnos(Integer profesionalId, Integer clienteId, LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new DatosInvalidosException("La fecha 'desde' no puede ser posterior a 'hasta'.");
        }
        return turnos.stream()
                .filter(t -> profesionalId == null || t.getProfesional().getId() == profesionalId)
                .filter(t -> clienteId == null || t.getCliente().getId() == clienteId)
                .filter(t -> desde == null || !t.getFecha().isBefore(desde))
                .filter(t -> hasta == null || !t.getFecha().isAfter(hasta))
                .sorted(Comparator.comparing(Turno::getInicio))
                .collect(Collectors.toList());
    }

    public List<Cliente> listarClientes() { return List.copyOf(clientes); }
    public List<Profesional> listarProfesionales() { return List.copyOf(profesionales); }

    // ---- internos ----
    private void validarReglas(SolicitudTurno solicitud) {
        for (ReglaTurno regla : reglas) {
            regla.validar(solicitud, turnos);
        }
    }

    private void exigirFechaHora(LocalDate fecha, LocalTime hora) {
        if (fecha == null || hora == null) {
            throw new DatosInvalidosException("La fecha y la hora son obligatorias.");
        }
    }

    private Cliente buscarCliente(int id) {
        return clientes.stream().filter(c -> c.getId() == id).findFirst()
                .orElseThrow(() -> new NoEncontradoException("No existe un cliente con id " + id + "."));
    }

    private Profesional buscarProfesional(int id) {
        return profesionales.stream().filter(p -> p.getId() == id).findFirst()
                .orElseThrow(() -> new NoEncontradoException("No existe un profesional con id " + id + "."));
    }

    private Turno buscarTurno(int id) {
        return turnos.stream().filter(t -> t.getId() == id).findFirst()
                .orElseThrow(() -> new NoEncontradoException("No existe un turno con id " + id + "."));
    }
}
