package com.pamplona.turnos.adaptadores.entrada.consola;

import com.pamplona.turnos.aplicacion.puertos.entrada.CasosDeUso;
import com.pamplona.turnos.aplicacion.puertos.entrada.Comandos;
import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;
import com.pamplona.turnos.dominio.Genero;
import com.pamplona.turnos.excepciones.DatosInvalidosException;
import com.pamplona.turnos.excepciones.TurnosException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 * Adaptador de entrada de consola (HU-7: CLI). Pide iniciar sesion, lee datos, llama a los casos de uso
 * y muestra resultados. Cada operacion informa su tiempo (HU-9). Sin reglas de negocio.
 */
public class ConsolaCli {

    private final CasosDeUso casos;
    private final Scanner in = new Scanner(System.in);
    private UsuarioAutenticado usuario;

    public ConsolaCli(CasosDeUso casos) {
        this.casos = casos;
    }

    public void iniciar() {
        System.out.println("=== SISTEMA DE GENERACION DE TURNOS (CLI) ===");
        try {
            if (!iniciarSesion()) {
                System.out.println("No se pudo iniciar sesion. Hasta luego.");
                return;
            }
            System.out.println("Sesion iniciada: " + usuario.usuario() + " (" + usuario.rolesTexto() + ")");
            bucle();
        } catch (NoSuchElementException fin) {
            System.out.println("\nEntrada finalizada.");
        }
    }

    private boolean iniciarSesion() {
        for (int intento = 1; intento <= 3; intento++) {
            String nombre = leer("Usuario");
            String clave = leer("Clave");
            try {
                usuario = casos.iniciarSesion().ejecutar(nombre, clave);
                return true;
            } catch (TurnosException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
        return false;
    }

    private void bucle() {
        while (true) {
            System.out.println();
            if (usuario.esAdmin()) {
                System.out.println("1. Registrar cliente");
                System.out.println("2. Registrar profesional");
                System.out.println("3. Crear turno");
                System.out.println("4. Cancelar turno");
                System.out.println("5. Reprogramar turno");
            }
            System.out.println("6. Consultar turnos");
            System.out.println("7. Listar clientes y profesionales");
            System.out.println("0. Salir");
            String opcion = leer("Opcion");
            if (opcion.equals("0")) {
                System.out.println("Hasta luego.");
                return;
            }
            ejecutar(opcion);
        }
    }

    private void ejecutar(String opcion) {
        boolean soloAdmin = opcion.matches("[1-5]");
        if (soloAdmin && !usuario.esAdmin()) {
            System.out.println("Opcion invalida.");
            return;
        }
        try {
            switch (opcion) {
                case "1" -> registrarCliente();
                case "2" -> registrarProfesional();
                case "3" -> crearTurno();
                case "4" -> cancelarTurno();
                case "5" -> reprogramarTurno();
                case "6" -> consultarTurnos();
                case "7" -> listarTodo();
                default -> System.out.println("Opcion invalida.");
            }
        } catch (TurnosException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("ERROR: ocurrio un problema inesperado. Intente de nuevo.");
        }
    }

    private void registrarCliente() {
        var comando = new Comandos.RegistrarCliente(leer("Nombre completo"), leer("Documento"), leer("Telefono"),
                leer("Correo"), Genero.desde(leer("Genero (M/F/O)")));
        System.out.println("Cliente registrado: " + medir(() -> casos.registrarCliente().ejecutar(usuario, comando)));
    }

    private void registrarProfesional() {
        var comando = new Comandos.RegistrarProfesional(leer("Nombre completo"), leer("Documento"), leer("Telefono"),
                leer("Correo"), Genero.desde(leer("Genero (M/F/O)")),
                leerHora("Disponible desde (HH:mm)"), leerHora("Disponible hasta (HH:mm)"));
        System.out.println("Profesional registrado: " + medir(() -> casos.registrarProfesional().ejecutar(usuario, comando)));
    }

    private void crearTurno() {
        var comando = new Comandos.CrearTurno(leerEntero("Id del cliente"), leerEntero("Id del profesional"),
                leerFecha("Fecha (AAAA-MM-DD)"), leerHora("Hora (HH:mm)"));
        System.out.println("Turno creado: " + medir(() -> casos.crearTurno().ejecutar(usuario, comando)));
    }

    private void cancelarTurno() {
        int id = leerEntero("Id del turno");
        System.out.println("Turno cancelado: " + medir(() -> casos.cancelarTurno().ejecutar(usuario, id)));
    }

    private void reprogramarTurno() {
        var comando = new Comandos.ReprogramarTurno(leerEntero("Id del turno"), leerFecha("Nueva fecha (AAAA-MM-DD)"),
                leerHora("Nueva hora (HH:mm)"));
        System.out.println("Turno reprogramado: " + medir(() -> casos.reprogramarTurno().ejecutar(usuario, comando)));
    }

    private void consultarTurnos() {
        System.out.println("Deje vacio los filtros que no quiera usar.");
        var filtro = new Comandos.FiltroTurnos(leerEnteroOpcional("Id del profesional"), leerEnteroOpcional("Id del cliente"),
                leerFechaOpcional("Desde (AAAA-MM-DD)"), leerFechaOpcional("Hasta (AAAA-MM-DD)"));
        List<?> lista = medir(() -> casos.consultarTurnos().ejecutar(usuario, filtro));
        if (lista.isEmpty()) {
            System.out.println("No hay turnos para esos filtros.");
        }
        lista.forEach(System.out::println);
    }

    private void listarTodo() {
        System.out.println("-- Clientes --");
        casos.listarClientes().ejecutar(usuario).forEach(System.out::println);
        System.out.println("-- Profesionales --");
        casos.listarProfesionales().ejecutar(usuario).forEach(System.out::println);
    }

    /** HU-9: muestra cuanto tarda la operacion (sin contar lo que el usuario tarda escribiendo). */
    private <T> T medir(Supplier<T> operacion) {
        long inicio = System.nanoTime();
        try {
            return operacion.get();
        } finally {
            double ms = (System.nanoTime() - inicio) / 1_000_000.0;
            System.out.printf("(operacion ejecutada en %.2f ms - %s)%n", ms, ms < 1000 ? "cumple < 1 s" : "supera 1 s");
        }
    }

    // ---- lectura y conversion ----
    private String leer(String etiqueta) {
        System.out.print(etiqueta + ": ");
        return in.nextLine().trim();
    }

    private int leerEntero(String etiqueta) {
        return aEntero(leer(etiqueta));
    }

    private Integer leerEnteroOpcional(String etiqueta) {
        String s = leer(etiqueta);
        return s.isEmpty() ? null : aEntero(s);
    }

    private int aEntero(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new DatosInvalidosException("'" + s + "' no es un numero valido.");
        }
    }

    private LocalDate leerFecha(String etiqueta) {
        return aFecha(leer(etiqueta));
    }

    private LocalDate leerFechaOpcional(String etiqueta) {
        String s = leer(etiqueta);
        return s.isEmpty() ? null : aFecha(s);
    }

    private LocalDate aFecha(String s) {
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            throw new DatosInvalidosException("Fecha invalida. Use el formato AAAA-MM-DD (ej. 2026-10-25).");
        }
    }

    private LocalTime leerHora(String etiqueta) {
        String s = leer(etiqueta);
        try {
            return LocalTime.parse(s);
        } catch (DateTimeParseException e) {
            throw new DatosInvalidosException("Hora invalida. Use el formato HH:mm (ej. 14:30).");
        }
    }
}
