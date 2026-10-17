package com.pamplona.turnos.adaptadores.entrada.swing.controlador;

import com.pamplona.turnos.adaptadores.entrada.swing.SesionSwing;
import com.pamplona.turnos.adaptadores.entrada.swing.vista.Mensajes;
import com.pamplona.turnos.adaptadores.entrada.swing.vista.ProfesionalPanel;
import com.pamplona.turnos.aplicacion.puertos.entrada.CasosDeUso;
import com.pamplona.turnos.aplicacion.puertos.entrada.Comandos;
import com.pamplona.turnos.dominio.Profesional;

/** Adaptador de entrada (ventana de profesionales). */
public class ProfesionalController extends ControladorBase {

    private final ProfesionalPanel vista;
    private final CasosDeUso casos;
    private final SesionSwing sesion;

    public ProfesionalController(ProfesionalPanel vista, CasosDeUso casos, SesionSwing sesion) {
        this.vista = vista;
        this.casos = casos;
        this.sesion = sesion;
        vista.addRegistrarListener(e -> ejecutar(this::registrar));
        ejecutar(this::refrescar);
    }

    public void registrar() {
        Profesional nuevo = casos.registrarProfesional().ejecutar(sesion.getUsuario(), new Comandos.RegistrarProfesional(
                vista.getNombre(), vista.getDocumento(), vista.getTelefono(), vista.getCorreo(), vista.getGenero(),
                hora(vista.getHoraInicio(), "La hora de inicio"), hora(vista.getHoraFin(), "La hora de fin")));
        vista.limpiar();
        refrescar();
        Mensajes.info("Profesional registrado: " + nuevo);
    }

    public void refrescar() {
        vista.mostrarProfesionales(casos.listarProfesionales().ejecutar(sesion.getUsuario()));
    }
}
