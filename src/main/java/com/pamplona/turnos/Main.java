package com.pamplona.turnos;

import com.pamplona.turnos.adaptadores.entrada.consola.ConsolaCli;
import com.pamplona.turnos.adaptadores.entrada.rest.ApiRest;
import com.pamplona.turnos.adaptadores.entrada.swing.SesionSwing;
import com.pamplona.turnos.adaptadores.entrada.swing.controlador.ClienteController;
import com.pamplona.turnos.adaptadores.entrada.swing.controlador.LoginController;
import com.pamplona.turnos.adaptadores.entrada.swing.controlador.ProfesionalController;
import com.pamplona.turnos.adaptadores.entrada.swing.controlador.TurnoController;
import com.pamplona.turnos.adaptadores.entrada.swing.vista.LoginFrame;
import com.pamplona.turnos.adaptadores.entrada.swing.vista.MainFrame;
import com.pamplona.turnos.aplicacion.puertos.entrada.CasosDeUso;
import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;
import com.pamplona.turnos.config.Configuracion;
import com.pamplona.turnos.config.Ensamblador;

import javax.swing.SwingUtilities;
import java.nio.file.Path;
import java.time.Clock;

/**
 * Elige por cual puerta de entrada se usa el mismo sistema:
 *   (sin argumentos) o "gui"  -> ventanas
 *   "cli"                     -> consola
 *   "api"                     -> API REST (puerto 8082 por defecto)
 * Segundo argumento opcional: carpeta de datos (por defecto "data").
 */
public class Main {

    public static void main(String[] args) throws Exception {
        String modo = args.length > 0 ? args[0].toLowerCase() : "gui";
        Path carpeta = Path.of(args.length > 1 ? args[1] : "data");
        Clock reloj = Clock.systemDefaultZone();

        Ensamblador.Sistema sistema = Ensamblador.armar(carpeta, Configuracion.urlAuthcore(), Configuracion.secretoJwt(), reloj);

        switch (modo) {
            case "gui" -> iniciarVentanas(sistema.casos());
            case "cli" -> new ConsolaCli(sistema.casos()).iniciar();
            case "api" -> {
                ApiRest api = new ApiRest(Configuracion.puertoApi(), sistema.casos(), sistema.validador());
                api.iniciar();
                System.out.println("API REST escuchando en http://localhost:" + api.puerto() + "/api/salud");
                System.out.println("Use el token del authcore: Authorization: Bearer <token>. Ctrl+C para detener.");
            }
            default -> System.out.println("Modo desconocido: '" + modo + "'. Use gui, cli o api.");
        }
    }

    private static void iniciarVentanas(CasosDeUso casos) {
        SwingUtilities.invokeLater(() -> {
            SesionSwing sesion = new SesionSwing();
            LoginFrame login = new LoginFrame(Configuracion.urlAuthcore());
            new LoginController(login, casos.iniciarSesion(), usuario -> {
                sesion.setUsuario(usuario);
                login.dispose();
                abrirAplicacion(usuario, casos, sesion);
            });
            login.setVisible(true);
        });
    }

    private static void abrirAplicacion(UsuarioAutenticado usuario, CasosDeUso casos, SesionSwing sesion) {
        MainFrame frame = new MainFrame(usuario);
        if (usuario.esAdmin()) {
            new ClienteController(frame.getClientePanel(), casos, sesion);
            new ProfesionalController(frame.getProfesionalPanel(), casos, sesion);
        }
        TurnoController turnos = new TurnoController(frame.getTurnoPanel(), frame.getConsultaPanel(), casos, sesion);
        frame.addCambioPestanaListener(e -> turnos.refrescar());
        frame.setVisible(true);
    }
}
