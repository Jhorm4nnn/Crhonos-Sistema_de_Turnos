package com.pamplona.turnos.presentacion.controlador;

import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.negocio.ProfesionalService;
import com.pamplona.turnos.presentacion.vista.Mensajes;
import com.pamplona.turnos.presentacion.vista.ProfesionalPanel;

/** Conecta la vista de profesionales con el servicio. No contiene reglas de negocio. */
public class ProfesionalController extends ControladorBase {

    private final ProfesionalPanel vista;
    private final ProfesionalService servicio;

    public ProfesionalController(ProfesionalPanel vista, ProfesionalService servicio) {
        this.vista = vista;
        this.servicio = servicio;
        vista.addRegistrarListener(e -> ejecutar(this::registrar));
        ejecutar(this::refrescar);
    }

    public void registrar() {
        Profesional nuevo = servicio.registrar(vista.getNombre(), vista.getDocumento(), vista.getTelefono(),
                vista.getCorreo(), vista.getGenero(),
                hora(vista.getHoraInicio(), "La hora de inicio"), hora(vista.getHoraFin(), "La hora de fin"));
        vista.limpiar();
        refrescar();
        Mensajes.info("Profesional registrado: " + nuevo);
    }

    public void refrescar() {
        vista.mostrarProfesionales(servicio.listar());
    }
}
