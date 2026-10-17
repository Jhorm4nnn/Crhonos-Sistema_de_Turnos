package com.pamplona.turnos.controlador;

import com.pamplona.turnos.excepciones.DuplicadoException;
import com.pamplona.turnos.modelo.HorarioDisponibilidad;
import com.pamplona.turnos.modelo.Profesional;
import com.pamplona.turnos.modelo.dao.ProfesionalDAO;
import com.pamplona.turnos.vista.Mensajes;
import com.pamplona.turnos.vista.ProfesionalPanel;

/** HU-2: registro de profesionales con su horario de disponibilidad. */
public class ProfesionalController extends ControladorBase {

    private final ProfesionalPanel vista;
    private final ProfesionalDAO dao;

    public ProfesionalController(ProfesionalPanel vista, ProfesionalDAO dao) {
        this.vista = vista;
        this.dao = dao;
        vista.addRegistrarListener(e -> ejecutar(this::registrar));
        refrescar();
    }

    public void registrar() {
        String documento = vista.getDocumento().trim();
        if (dao.buscarPorDocumento(documento).isPresent()) {
            throw new DuplicadoException("Ya existe un profesional con el documento " + documento + ".");
        }
        HorarioDisponibilidad horario = new HorarioDisponibilidad(
                hora(vista.getHoraInicio(), "La hora de inicio"), hora(vista.getHoraFin(), "La hora de fin"));
        Profesional nuevo = dao.crear(vista.getNombre(), documento, vista.getTelefono(), vista.getCorreo(),
                vista.getGenero(), horario);
        vista.limpiar();
        refrescar();
        Mensajes.info("Profesional registrado: " + nuevo);
    }

    public void refrescar() {
        vista.mostrarProfesionales(dao.listar());
    }
}
