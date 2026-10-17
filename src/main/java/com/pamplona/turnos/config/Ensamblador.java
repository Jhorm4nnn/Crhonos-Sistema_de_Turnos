package com.pamplona.turnos.config;

import com.pamplona.turnos.adaptadores.salida.persistencia.ClienteRepositoryArchivo;
import com.pamplona.turnos.adaptadores.salida.persistencia.ProfesionalRepositoryArchivo;
import com.pamplona.turnos.adaptadores.salida.persistencia.TurnoRepositoryArchivo;
import com.pamplona.turnos.adaptadores.salida.seguridad.AuthCoreHttpClient;
import com.pamplona.turnos.adaptadores.salida.seguridad.JwtHs256Validador;
import com.pamplona.turnos.aplicacion.puertos.entrada.CasosDeUso;
import com.pamplona.turnos.aplicacion.puertos.salida.AutenticadorPort;
import com.pamplona.turnos.aplicacion.puertos.salida.ClienteRepositoryPort;
import com.pamplona.turnos.aplicacion.puertos.salida.ProfesionalRepositoryPort;
import com.pamplona.turnos.aplicacion.puertos.salida.TurnoRepositoryPort;
import com.pamplona.turnos.aplicacion.puertos.salida.ValidadorTokenPort;
import com.pamplona.turnos.aplicacion.reglas.ReglaDisponibilidad;
import com.pamplona.turnos.aplicacion.reglas.ReglaFechaFutura;
import com.pamplona.turnos.aplicacion.reglas.ReglaSinCruce;
import com.pamplona.turnos.aplicacion.reglas.ReglaTurno;
import com.pamplona.turnos.aplicacion.servicios.ClienteService;
import com.pamplona.turnos.aplicacion.servicios.ConsultaTurnosService;
import com.pamplona.turnos.aplicacion.servicios.IniciarSesionService;
import com.pamplona.turnos.aplicacion.servicios.ProfesionalService;
import com.pamplona.turnos.aplicacion.servicios.TurnoService;

import java.nio.file.Path;
import java.time.Clock;
import java.util.List;

/**
 * Raiz de composicion: el UNICO lugar donde se eligen los adaptadores concretos y se conectan con la aplicacion.
 * Para cambiar de archivo a MySQL, o de authcore local a AWS, solo se toca aqui (o la configuracion).
 */
public final class Ensamblador {

    public record Sistema(CasosDeUso casos, ValidadorTokenPort validador) { }

    private Ensamblador() { }

    public static Sistema armar(Path carpetaDatos, String urlAuthcore, String secretoJwt, Clock reloj) {
        // adaptadores de salida
        ClienteRepositoryPort clientes = new ClienteRepositoryArchivo(carpetaDatos);
        ProfesionalRepositoryPort profesionales = new ProfesionalRepositoryArchivo(carpetaDatos);
        TurnoRepositoryPort turnos = new TurnoRepositoryArchivo(carpetaDatos, clientes, profesionales);
        AutenticadorPort autenticador = new AuthCoreHttpClient(urlAuthcore);
        ValidadorTokenPort validador = new JwtHs256Validador(secretoJwt, reloj);

        // aplicacion (casos de uso)
        List<ReglaTurno> reglas = List.of(new ReglaFechaFutura(reloj), new ReglaDisponibilidad(), new ReglaSinCruce());
        ClienteService clienteService = new ClienteService(clientes, reloj);
        ProfesionalService profesionalService = new ProfesionalService(profesionales, reloj);
        TurnoService turnoService = new TurnoService(turnos, clientes, profesionales, reglas, reloj);
        ConsultaTurnosService consultaService = new ConsultaTurnosService(turnos, reloj);

        CasosDeUso casos = new CasosDeUso(
                new IniciarSesionService(autenticador, validador),
                clienteService, clienteService,
                profesionalService, profesionalService,
                turnoService, turnoService, turnoService,
                consultaService);
        return new Sistema(casos, validador);
    }
}
