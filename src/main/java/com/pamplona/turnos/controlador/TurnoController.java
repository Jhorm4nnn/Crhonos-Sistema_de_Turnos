package com.pamplona.turnos.controlador;

import com.pamplona.turnos.excepciones.CruceDeHorarioException;
import com.pamplona.turnos.excepciones.DatosInvalidosException;
import com.pamplona.turnos.excepciones.NoEncontradoException;
import com.pamplona.turnos.excepciones.ReglaTurnoException;
import com.pamplona.turnos.modelo.Cliente;
import com.pamplona.turnos.modelo.Profesional;
import com.pamplona.turnos.modelo.Turno;
import com.pamplona.turnos.modelo.dao.ClienteDAO;
import com.pamplona.turnos.modelo.dao.ProfesionalDAO;
import com.pamplona.turnos.modelo.dao.TurnoDAO;
import com.pamplona.turnos.vista.ConsultaPanel;
import com.pamplona.turnos.vista.Mensajes;
import com.pamplona.turnos.vista.TurnoPanel;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HU-3, HU-4, HU-5, HU-6: gestion y consulta de turnos.
 * En MVC las reglas de negocio viven aqui, en el controlador (en la fase 3 pasan a servicios).
 */
public class TurnoController extends ControladorBase {

    private final TurnoPanel vistaTurnos;
    private final ConsultaPanel vistaConsulta;
    private final TurnoDAO turnoDAO;
    private final ClienteDAO clienteDAO;
    private final ProfesionalDAO profesionalDAO;
    private final Clock reloj;

    public TurnoController(TurnoPanel vistaTurnos, ConsultaPanel vistaConsulta, TurnoDAO turnoDAO,
                           ClienteDAO clienteDAO, ProfesionalDAO profesionalDAO, Clock reloj) {
        this.vistaTurnos = vistaTurnos;
        this.vistaConsulta = vistaConsulta;
        this.turnoDAO = turnoDAO;
        this.clienteDAO = clienteDAO;
        this.profesionalDAO = profesionalDAO;
        this.reloj = reloj;

        vistaTurnos.addCrearListener(e -> ejecutar(this::crear));
        vistaTurnos.addCancelarListener(e -> ejecutar(this::cancelar));
        vistaTurnos.addReprogramarListener(e -> ejecutar(this::reprogramar));
        vistaConsulta.addBuscarListener(e -> ejecutar(this::consultar));
        vistaConsulta.addLimpiarListener(e -> ejecutar(() -> {
            vistaConsulta.limpiarFiltros();
            consultar();
        }));
        refrescar();
    }

    /** HU-3 y HU-4: crea el turno validando datos, fecha futura, disponibilidad y cruces. */
    public void crear() {
        Cliente cliente = vistaTurnos.getClienteSeleccionado();
        Profesional prof = vistaTurnos.getProfesionalSeleccionado();
        if (cliente == null) {
            throw new DatosInvalidosException("Debe seleccionar un cliente.");
        }
        if (prof == null) {
            throw new DatosInvalidosException("Debe seleccionar un profesional.");
        }
        LocalDate fecha = fecha(vistaTurnos.getFecha(), "La fecha");
        LocalTime hora = hora(vistaTurnos.getHora(), "La hora");
        validarHorario(prof, fecha, hora, null);

        Turno t = turnoDAO.crear(cliente, prof, fecha, hora);
        vistaTurnos.limpiarFormulario();
        refrescar();
        Mensajes.info("Turno creado: " + t);
    }

    /** HU-5: cancela el turno seleccionado. */
    public void cancelar() {
        Turno t = turnoSeleccionado();
        t.cancelar();
        turnoDAO.actualizar(t);
        refrescar();
        Mensajes.info("Turno cancelado: " + t);
    }

    /** HU-5: reprograma el turno seleccionado y vuelve a validar el cruce. */
    public void reprogramar() {
        Turno t = turnoSeleccionado();
        LocalDate fecha = fecha(vistaTurnos.getNuevaFecha(), "La nueva fecha");
        LocalTime hora = hora(vistaTurnos.getNuevaHora(), "La nueva hora");
        validarHorario(t.getProfesional(), fecha, hora, t);

        t.reprogramar(fecha, hora);
        turnoDAO.actualizar(t);
        vistaTurnos.limpiarReprogramacion();
        refrescar();
        Mensajes.info("Turno reprogramado: " + t);
    }

    /** HU-6: consulta con filtros combinables (profesional, cliente, rango de fechas). */
    public void consultar() {
        Profesional prof = vistaConsulta.getProfesionalSeleccionado();
        Cliente cli = vistaConsulta.getClienteSeleccionado();
        LocalDate desde = fechaOpcional(vistaConsulta.getDesde(), "La fecha 'desde'");
        LocalDate hasta = fechaOpcional(vistaConsulta.getHasta(), "La fecha 'hasta'");
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new DatosInvalidosException("La fecha 'desde' no puede ser posterior a 'hasta'.");
        }
        List<Turno> resultado = turnoDAO.listar().stream()
                .filter(t -> prof == null || t.getProfesional().getId() == prof.getId())
                .filter(t -> cli == null || t.getCliente().getId() == cli.getId())
                .filter(t -> desde == null || !t.getFecha().isBefore(desde))
                .filter(t -> hasta == null || !t.getFecha().isAfter(hasta))
                .sorted(Comparator.comparing(Turno::getInicio))
                .collect(Collectors.toList());
        vistaConsulta.mostrarTurnos(aFilas(resultado));
    }

    /** Recarga combos y tablas desde el modelo (se llama al cambiar de pestana). */
    public void refrescar() {
        List<Cliente> clientes = clienteDAO.listar();
        List<Profesional> profesionales = profesionalDAO.listar();
        vistaTurnos.setClientes(clientes);
        vistaTurnos.setProfesionales(profesionales);
        vistaConsulta.setClientes(clientes);
        vistaConsulta.setProfesionales(profesionales);

        List<Turno> todos = turnoDAO.listar().stream().sorted(Comparator.comparing(Turno::getInicio))
                .collect(Collectors.toList());
        vistaTurnos.mostrarTurnos(aFilas(todos));
        vistaConsulta.mostrarTurnos(aFilas(todos));
    }

    // ---- reglas de negocio (en MVC viven en el controlador) ----

    private void validarHorario(Profesional prof, LocalDate fecha, LocalTime hora, Turno ignorar) {
        if (!LocalDateTime.of(fecha, hora).isAfter(LocalDateTime.now(reloj))) {
            throw new ReglaTurnoException("La fecha y hora deben ser posteriores a la actual.");
        }
        if (!prof.getHorario().cubre(hora, Turno.DURACION_MIN)) {
            throw new ReglaTurnoException("El horario esta fuera de la disponibilidad del profesional ("
                    + prof.getHorario() + ").");
        }
        for (Turno t : turnoDAO.listar()) {
            boolean mismo = ignorar != null && t.getId() == ignorar.getId();
            if (!mismo && t.estaAgendado() && t.getProfesional().equals(prof) && t.seCruzaCon(fecha, hora)) {
                throw new CruceDeHorarioException("El profesional ya tiene el turno #" + t.getId() + " que se cruza ("
                        + t.getFecha() + " " + t.getHora() + " a " + t.getHoraFin() + ").");
            }
        }
    }

    private Turno turnoSeleccionado() {
        Integer id = vistaTurnos.getTurnoSeleccionadoId();
        if (id == null) {
            throw new DatosInvalidosException("Seleccione un turno de la tabla.");
        }
        Turno t = turnoDAO.buscarPorId(id)
                .orElseThrow(() -> new NoEncontradoException("No existe un turno con id " + id + "."));
        t.exigirAgendado();
        return t;
    }

    private List<Object[]> aFilas(List<Turno> turnos) {
        List<Object[]> filas = new ArrayList<>();
        for (Turno t : turnos) {
            filas.add(new Object[]{t.getId(), t.getCliente().getNombreCompleto(), t.getProfesional().getNombreCompleto(),
                t.getFecha().toString(), t.getHora() + "-" + t.getHoraFin(), t.getEstado().toString()});
        }
        return filas;
    }
}
