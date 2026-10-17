package com.pamplona.turnos.adaptadores.entrada.swing.controlador;

import com.pamplona.turnos.adaptadores.entrada.swing.SesionSwing;
import com.pamplona.turnos.adaptadores.entrada.swing.vista.ConsultaPanel;
import com.pamplona.turnos.adaptadores.entrada.swing.vista.Mensajes;
import com.pamplona.turnos.adaptadores.entrada.swing.vista.TurnoPanel;
import com.pamplona.turnos.aplicacion.puertos.entrada.CasosDeUso;
import com.pamplona.turnos.aplicacion.puertos.entrada.Comandos;
import com.pamplona.turnos.dominio.Cliente;
import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.dominio.Turno;

import java.util.ArrayList;
import java.util.List;

/** Adaptador de entrada (ventanas de turnos y consulta). */
public class TurnoController extends ControladorBase {

    private final TurnoPanel vistaTurnos;
    private final ConsultaPanel vistaConsulta;
    private final CasosDeUso casos;
    private final SesionSwing sesion;

    public TurnoController(TurnoPanel vistaTurnos, ConsultaPanel vistaConsulta, CasosDeUso casos, SesionSwing sesion) {
        this.vistaTurnos = vistaTurnos;
        this.vistaConsulta = vistaConsulta;
        this.casos = casos;
        this.sesion = sesion;

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
        Turno t = casos.crearTurno().ejecutar(sesion.getUsuario(), new Comandos.CrearTurno(
                c == null ? null : c.getId(), p == null ? null : p.getId(),
                fecha(vistaTurnos.getFecha(), "La fecha"), hora(vistaTurnos.getHora(), "La hora")));
        vistaTurnos.limpiarFormulario();
        refrescar();
        Mensajes.info("Turno creado: " + t);
    }

    public void cancelar() {
        Turno t = casos.cancelarTurno().ejecutar(sesion.getUsuario(), vistaTurnos.getTurnoSeleccionadoId());
        refrescar();
        Mensajes.info("Turno cancelado: " + t);
    }

    public void reprogramar() {
        Turno t = casos.reprogramarTurno().ejecutar(sesion.getUsuario(), new Comandos.ReprogramarTurno(
                vistaTurnos.getTurnoSeleccionadoId(),
                fecha(vistaTurnos.getNuevaFecha(), "La nueva fecha"), hora(vistaTurnos.getNuevaHora(), "La nueva hora")));
        vistaTurnos.limpiarReprogramacion();
        refrescar();
        Mensajes.info("Turno reprogramado: " + t);
    }

    public void consultar() {
        Profesional p = vistaConsulta.getProfesionalSeleccionado();
        Cliente c = vistaConsulta.getClienteSeleccionado();
        List<Turno> lista = casos.consultarTurnos().ejecutar(sesion.getUsuario(), new Comandos.FiltroTurnos(
                p == null ? null : p.getId(), c == null ? null : c.getId(),
                fechaOpcional(vistaConsulta.getDesde(), "La fecha 'desde'"),
                fechaOpcional(vistaConsulta.getHasta(), "La fecha 'hasta'")));
        vistaConsulta.mostrarTurnos(aFilas(lista));
    }

    /** Recarga combos y tablas (se llama al cambiar de pestana). */
    public void refrescar() {
        var usuario = sesion.getUsuario();
        List<Cliente> clientes = casos.listarClientes().ejecutar(usuario);
        List<Profesional> profesionales = casos.listarProfesionales().ejecutar(usuario);
        vistaTurnos.setClientes(clientes);
        vistaTurnos.setProfesionales(profesionales);
        vistaConsulta.setClientes(clientes);
        vistaConsulta.setProfesionales(profesionales);

        List<Object[]> todos = aFilas(casos.consultarTurnos().ejecutar(usuario, new Comandos.FiltroTurnos(null, null, null, null)));
        vistaTurnos.mostrarTurnos(todos);
        vistaConsulta.mostrarTurnos(todos);
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
