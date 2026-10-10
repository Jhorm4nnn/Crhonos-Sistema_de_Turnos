package com.pamplona.turnos.presentacion.controlador;

import com.pamplona.turnos.dominio.Cliente;
import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.negocio.ClienteService;
import com.pamplona.turnos.negocio.ProfesionalService;
import com.pamplona.turnos.negocio.TurnoConsultaService;
import com.pamplona.turnos.negocio.TurnoDetalle;
import com.pamplona.turnos.negocio.TurnoService;
import com.pamplona.turnos.presentacion.vista.ConsultaPanel;
import com.pamplona.turnos.presentacion.vista.Mensajes;
import com.pamplona.turnos.presentacion.vista.TurnoPanel;

import java.util.ArrayList;
import java.util.List;

/** Conecta las vistas de turnos y consulta con los servicios. No contiene reglas de negocio. */
public class TurnoController extends ControladorBase {

    private final TurnoPanel vistaTurnos;
    private final ConsultaPanel vistaConsulta;
    private final TurnoService turnos;
    private final TurnoConsultaService consultas;
    private final ClienteService clientes;
    private final ProfesionalService profesionales;

    public TurnoController(TurnoPanel vistaTurnos, ConsultaPanel vistaConsulta, TurnoService turnos,
                           TurnoConsultaService consultas, ClienteService clientes, ProfesionalService profesionales) {
        this.vistaTurnos = vistaTurnos;
        this.vistaConsulta = vistaConsulta;
        this.turnos = turnos;
        this.consultas = consultas;
        this.clientes = clientes;
        this.profesionales = profesionales;

        vistaTurnos.addCrearListener(e -> ejecutar(this::crear));
        vistaTurnos.addCancelarListener(e -> ejecutar(this::cancelar));
        vistaTurnos.addReprogramarListener(e -> ejecutar(this::reprogramar));
        vistaConsulta.addBuscarListener(e -> ejecutar(this::consultar));
        vistaConsulta.addLimpiarListener(e -> ejecutar(() -> {
            vistaConsulta.limpiarFiltros();
            consultar();
        }));
        ejecutar(this::refrescar);
    }

    public void crear() {
        Cliente c = vistaTurnos.getClienteSeleccionado();
        Profesional p = vistaTurnos.getProfesionalSeleccionado();
        TurnoDetalle t = turnos.crear(c == null ? null : c.getId(), p == null ? null : p.getId(),
                Entradas.fecha(vistaTurnos.getFecha(), "La fecha"), Entradas.hora(vistaTurnos.getHora(), "La hora"));
        vistaTurnos.limpiarFormulario();
        refrescar();
        Mensajes.info("Turno creado: " + t.resumen());
    }

    public void cancelar() {
        TurnoDetalle t = turnos.cancelar(vistaTurnos.getTurnoSeleccionadoId());
        refrescar();
        Mensajes.info("Turno cancelado: " + t.resumen());
    }

    public void reprogramar() {
        Integer id = vistaTurnos.getTurnoSeleccionadoId();
        TurnoDetalle t = turnos.reprogramar(id,
                Entradas.fecha(vistaTurnos.getNuevaFecha(), "La nueva fecha"),
                Entradas.hora(vistaTurnos.getNuevaHora(), "La nueva hora"));
        vistaTurnos.limpiarReprogramacion();
        refrescar();
        Mensajes.info("Turno reprogramado: " + t.resumen());
    }

    public void consultar() {
        Profesional p = vistaConsulta.getProfesionalSeleccionado();
        Cliente c = vistaConsulta.getClienteSeleccionado();
        List<TurnoDetalle> lista = consultas.consultar(p == null ? null : p.getId(), c == null ? null : c.getId(),
                Entradas.fechaOpcional(vistaConsulta.getDesde(), "La fecha 'desde'"),
                Entradas.fechaOpcional(vistaConsulta.getHasta(), "La fecha 'hasta'"));
        vistaConsulta.mostrarTurnos(aFilas(lista));
    }

    /** Recarga combos y tablas (se llama al cambiar de pestana). */
    public void refrescar() {
        List<Cliente> listaClientes = clientes.listar();
        List<Profesional> listaProfesionales = profesionales.listar();
        vistaTurnos.setClientes(listaClientes);
        vistaTurnos.setProfesionales(listaProfesionales);
        vistaConsulta.setClientes(listaClientes);
        vistaConsulta.setProfesionales(listaProfesionales);

        List<Object[]> todos = aFilas(consultas.listarTodos());
        vistaTurnos.mostrarTurnos(todos);
        vistaConsulta.mostrarTurnos(todos);
    }

    private List<Object[]> aFilas(List<TurnoDetalle> turnos) {
        List<Object[]> filas = new ArrayList<>();
        for (TurnoDetalle t : turnos) {
            filas.add(new Object[]{t.id(), t.cliente(), t.profesional(), t.fecha().toString(),
                t.hora() + "-" + t.horaFin(), t.estado().toString()});
        }
        return filas;
    }
}
