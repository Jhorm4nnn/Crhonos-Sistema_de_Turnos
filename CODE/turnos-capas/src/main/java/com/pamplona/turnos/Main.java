package com.pamplona.turnos;

import com.pamplona.turnos.datos.AuthCoreHttpClient;
import com.pamplona.turnos.datos.AuthGateway;
import com.pamplona.turnos.datos.ClienteRepository;
import com.pamplona.turnos.datos.ClienteRepositoryArchivo;
import com.pamplona.turnos.datos.Configuracion;
import com.pamplona.turnos.datos.ProfesionalRepository;
import com.pamplona.turnos.datos.ProfesionalRepositoryArchivo;
import com.pamplona.turnos.datos.TurnoRepository;
import com.pamplona.turnos.datos.TurnoRepositoryArchivo;
import com.pamplona.turnos.negocio.AuthService;
import com.pamplona.turnos.negocio.ClienteService;
import com.pamplona.turnos.negocio.ProfesionalService;
import com.pamplona.turnos.negocio.ReglaDisponibilidad;
import com.pamplona.turnos.negocio.ReglaFechaFutura;
import com.pamplona.turnos.negocio.ReglaSinCruce;
import com.pamplona.turnos.negocio.ReglaTurno;
import com.pamplona.turnos.negocio.Sesion;
import com.pamplona.turnos.negocio.TurnoConsultaService;
import com.pamplona.turnos.negocio.TurnoService;
import com.pamplona.turnos.negocio.UsuarioAutenticado;
import com.pamplona.turnos.presentacion.controlador.ClienteController;
import com.pamplona.turnos.presentacion.controlador.LoginController;
import com.pamplona.turnos.presentacion.controlador.ProfesionalController;
import com.pamplona.turnos.presentacion.controlador.TurnoController;
import com.pamplona.turnos.presentacion.vista.LoginFrame;
import com.pamplona.turnos.presentacion.vista.MainFrame;

import javax.swing.SwingUtilities;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;

/**
 * Raiz de composicion: aqui (y solo aqui) se crean las clases concretas y se conectan las capas
 * (principio de Inversion de Dependencias).
 */
public class Main {

    public static void main(String[] args) {
        Path carpeta = Path.of(args.length > 0 ? args[0] : "data");
        Clock reloj = Clock.systemDefaultZone();
        String urlAuthcore = Configuracion.urlAuthcore();

        // datos
        ClienteRepository clienteRepo = new ClienteRepositoryArchivo(carpeta);
        ProfesionalRepository profRepo = new ProfesionalRepositoryArchivo(carpeta);
        TurnoRepository turnoRepo = new TurnoRepositoryArchivo(carpeta);
        AuthGateway authGateway = new AuthCoreHttpClient(urlAuthcore);

        // negocio
        Sesion sesion = new Sesion(reloj);
        AuthService authService = new AuthService(authGateway, sesion);
        ClienteService clienteService = new ClienteService(clienteRepo, sesion);
        ProfesionalService profService = new ProfesionalService(profRepo, sesion);
        List<ReglaTurno> reglas = List.of(new ReglaFechaFutura(reloj), new ReglaDisponibilidad(),
                new ReglaSinCruce(turnoRepo));
        TurnoService turnoService = new TurnoService(turnoRepo, clienteRepo, profRepo, reglas, sesion);
        TurnoConsultaService consultaService = new TurnoConsultaService(turnoRepo, clienteRepo, profRepo, sesion);

        // presentacion
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame(urlAuthcore);
            new LoginController(login, authService, usuario -> {
                login.dispose();
                abrirAplicacion(usuario, clienteService, profService, turnoService, consultaService);
            });
            login.setVisible(true);
        });
    }

    private static void abrirAplicacion(UsuarioAutenticado usuario, ClienteService clienteService,
                                        ProfesionalService profService, TurnoService turnoService,
                                        TurnoConsultaService consultaService) {
        MainFrame frame = new MainFrame(usuario);
        if (usuario.esAdmin()) {
            new ClienteController(frame.getClientePanel(), clienteService);
            new ProfesionalController(frame.getProfesionalPanel(), profService);
        }
        TurnoController turnos = new TurnoController(frame.getTurnoPanel(), frame.getConsultaPanel(),
                turnoService, consultaService, clienteService, profService);
        frame.addCambioPestanaListener(e -> turnos.refrescar());
        frame.setVisible(true);
    }
}
