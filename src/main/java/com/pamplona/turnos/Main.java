package com.pamplona.turnos;

import com.pamplona.turnos.controlador.ClienteController;
import com.pamplona.turnos.controlador.ProfesionalController;
import com.pamplona.turnos.controlador.TurnoController;
import com.pamplona.turnos.modelo.dao.ClienteDAO;
import com.pamplona.turnos.modelo.dao.ProfesionalDAO;
import com.pamplona.turnos.modelo.dao.TurnoDAO;
import com.pamplona.turnos.vista.MainFrame;

import javax.swing.SwingUtilities;
import java.nio.file.Path;
import java.time.Clock;

/** Punto de entrada MVC: crea el modelo, la vista y conecta los controladores. */
public class Main {

    public static void main(String[] args) {
        Path carpeta = Path.of(args.length > 0 ? args[0] : "data");

        // Modelo
        ClienteDAO clienteDAO = new ClienteDAO(carpeta);
        ProfesionalDAO profesionalDAO = new ProfesionalDAO(carpeta);
        TurnoDAO turnoDAO = new TurnoDAO(carpeta, clienteDAO, profesionalDAO);

        SwingUtilities.invokeLater(() -> {
            // Vista
            MainFrame frame = new MainFrame();

            // Controladores
            new ClienteController(frame.getClientePanel(), clienteDAO);
            new ProfesionalController(frame.getProfesionalPanel(), profesionalDAO);
            TurnoController turnos = new TurnoController(frame.getTurnoPanel(), frame.getConsultaPanel(),
                    turnoDAO, clienteDAO, profesionalDAO, Clock.systemDefaultZone());
            frame.addCambioPestanaListener(e -> turnos.refrescar());

            frame.setVisible(true);
        });
    }
}
