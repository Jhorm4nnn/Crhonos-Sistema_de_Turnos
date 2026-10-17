package com.pamplona.turnos.consola;

import com.pamplona.turnos.dominio.Cliente;
import com.pamplona.turnos.dominio.Genero;
import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.dominio.Turno;
import com.pamplona.turnos.excepciones.DatosInvalidosException;
import com.pamplona.turnos.excepciones.TurnosException;
import com.pamplona.turnos.gestion.Agenda;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Interfaz de consola. Solo lee datos, llama a la Agenda y muestra resultados (HU-1 a HU-6, HU-8, HU-9). */
public class MenuConsola {

    private final Agenda agenda;
    private final Scanner in = new Scanner(System.in);

    public MenuConsola(Agenda agenda) {
        this.agenda = agenda;
    }

    public void iniciar() {
        System.out.println("=== SISTEMA DE GENERACION DE TURNOS (Fase 1 - POO) ===");
        try {
            while (true) {
                System.out.println("\n1. Registrar cliente");
                System.out.println("2. Registrar profesional");
                System.out.println("3. Crear turno");
                System.out.println("4. Cancelar turno");
                System.out.println("5. Reprogramar turno");
                System.out.println("6. Consultar turnos");
                System.out.println("7. Listar clientes y profesionales");
                System.out.println("8. Cargar datos de ejemplo");
                System.out.println("0. Salir");
                String opcion = leer("Opcion");
                if (opcion.equals("0")) {
                    System.out.println("Hasta luego.");
                    return;
                }
                ejecutar(opcion);
            }
        } catch (NoSuchElementException fin) {
            System.out.println("\nEntrada finalizada.");
        }
    }

    private void ejecutar(String opcion) {
        try {
            switch (opcion) {
                case "1" -> registrarCliente();
                case "2" -> registrarProfesional();
                case "3" -> crearTurno();
                case "4" -> cancelarTurno();
                case "5" -> reprogramarTurno();
                case "6" -> consultarTurnos();
                case "7" -> listarTodo();
                case "8" -> cargarEjemplo();
                default -> {
                    System.out.println("Opcion invalida.");
                    return;
                }
            }
        } catch (TurnosException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (RuntimeException e) {
            // HU-8: no se muestran detalles tecnicos.
            System.out.println("ERROR: ocurrio un problema inesperado. Intente de nuevo.");
        }
    }

    private void registrarCliente() {
        String nombre = leer("Nombre completo");
        String documento = leer("Documento");
        String telefono = leer("Telefono");
        String correo = leer("Correo");
        Genero genero = Genero.desde(leer("Genero (M/F/O)"));
        Cliente c = medir(() -> agenda.registrarCliente(nombre, documento, telefono, correo, genero));
        System.out.println("Cliente registrado: " + c);
    }

    private void registrarProfesional() {
        String nombre = leer("Nombre completo");
        String documento = leer("Documento");
        String telefono = leer("Telefono");
        String correo = leer("Correo");
        Genero genero = Genero.desde(leer("Genero (M/F/O)"));
        LocalTime desde = leerHora("Disponible desde (HH:mm)");
        LocalTime hasta = leerHora("Disponible hasta (HH:mm)");
        Profesional p = medir(() -> agenda.registrarProfesional(nombre, documento, telefono, correo, genero, desde, hasta));
        System.out.println("Profesional registrado: " + p);
    }

    private void crearTurno() {
        int cliente = leerEntero("Id del cliente");
        int profesional = leerEntero("Id del profesional");
        LocalDate fecha = leerFecha("Fecha (AAAA-MM-DD)");
        LocalTime hora = leerHora("Hora (HH:mm)");
        Turno t = medir(() -> agenda.crearTurno(cliente, profesional, fecha, hora));
        System.out.println("Turno creado: " + t);
    }

    private void cancelarTurno() {
        int id = leerEntero("Id del turno");
        Turno t = medir(() -> agenda.cancelarTurno(id));
        System.out.println("Turno cancelado: " + t);
    }

    private void reprogramarTurno() {
        int id = leerEntero("Id del turno");
        LocalDate fecha = leerFecha("Nueva fecha (AAAA-MM-DD)");
        LocalTime hora = leerHora("Nueva hora (HH:mm)");
        Turno t = medir(() -> agenda.reprogramarTurno(id, fecha, hora));
        System.out.println("Turno reprogramado: " + t);
    }

    private void consultarTurnos() {
        System.out.println("Deje vacio los filtros que no quiera usar.");
        Integer profesional = leerEnteroOpcional("Id del profesional");
        Integer cliente = leerEnteroOpcional("Id del cliente");
        LocalDate desde = leerFechaOpcional("Desde (AAAA-MM-DD)");
        LocalDate hasta = leerFechaOpcional("Hasta (AAAA-MM-DD)");
        List<Turno> lista = medir(() -> agenda.consultarTurnos(profesional, cliente, desde, hasta));
        if (lista.isEmpty()) {
            System.out.println("No hay turnos para esos filtros.");
        }
        lista.forEach(System.out::println);
    }

    private void listarTodo() {
        System.out.println("-- Clientes --");
        agenda.listarClientes().forEach(System.out::println);
        System.out.println("-- Profesionales --");
        agenda.listarProfesionales().forEach(System.out::println);
    }

    /** Datos de ejemplo para probar rapido (las fechas son de manana en adelante, siempre validas). */
    private void cargarEjemplo() {
        LocalDate manana = LocalDate.now().plusDays(1);
        agenda.registrarCliente("Juan Perez", "1001001001", "3001234567", "juan@correo.com", Genero.MASCULINO);
        agenda.registrarCliente("Maria Gomez", "1002002002", "3109876543", "maria@correo.com", Genero.FEMENINO);
        agenda.registrarProfesional("Laura Lopez", "52000111", "3205550000", "laura@correo.com", Genero.FEMENINO,
                LocalTime.of(8, 0), LocalTime.of(12, 0));
        agenda.crearTurno(1, 1, manana, LocalTime.of(9, 0));
        System.out.println("Datos de ejemplo cargados: 2 clientes, 1 profesional y 1 turno (" + manana + " 09:00).");
    }

    /** HU-9: mide y muestra cuanto tarda la operacion de negocio (sin contar lo que el usuario tarda escribiendo). */
    private <T> T medir(java.util.function.Supplier<T> operacion) {
        long inicio = System.nanoTime();
        try {
            return operacion.get();
        } finally {
            double ms = (System.nanoTime() - inicio) / 1_000_000.0;
            System.out.printf("(operacion ejecutada en %.2f ms - %s)%n", ms, ms < 1000 ? "cumple < 1 s" : "supera 1 s");
        }
    }

    // ---- lectura y conversion de entradas ----
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
